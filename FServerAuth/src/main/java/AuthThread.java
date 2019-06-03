import Resources.AccessOperation;
import Resources.Account;
import Utils.CryptoAESUtil;
import Utils.DSASignature;
import Utils.DiffieHellman;
import Utils.TokenUtil;

import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.Security;

public class AuthThread extends Thread {
    private static DataInputStream reader;
    private static DataOutputStream writer;
    private Socket socket;

    public AuthThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len - 1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }

    public void run2() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        int tmpBytesToRead;
        BufferedReader r = null;

        try {
            reader = new DataInputStream(this.socket.getInputStream());
            writer = new DataOutputStream(this.socket.getOutputStream());

            System.out.println(reader.readUTF());

            String username = reader.readUTF();
            String password = reader.readUTF();

            boolean isPWDValid = Authenticator.checkPassword(username, password);

            if (isPWDValid) {
                String token = Authenticator.generateToken(username);
                byte[] tokenBytes = token.getBytes();
                writer.writeInt(tokenBytes.length);
                writer.write(tokenBytes);

                System.out.println("Authenticated");
            }

            System.out.println("Socket closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        int tmpBytesToRead;
        BufferedReader r = null;

        try {
            reader = new DataInputStream(this.socket.getInputStream());
            writer = new DataOutputStream(this.socket.getOutputStream());

            System.out.println(reader.readUTF());

            //Get Alices pub key
            tmpBytesToRead = reader.readInt();
            byte[] alicePubKey = new byte[tmpBytesToRead];
            reader.readFully(alicePubKey);

            //Do DH
            DiffieHellman Bob = new DiffieHellman(alicePubKey); //Inicialize Bob with Alice's pubKey
            Bob.doPhase(); //Bob does the calculations

            //Send alice pub key
            writer.writeInt(Bob.getEncodedPublicKey().length);
            writer.write(Bob.getEncodedPublicKey());

            /*
             *  Done with DH now create cipher
             *
             */

            //Get params like IV
//            tmpBytesToRead = reader.readInt();
//            byte[] AliceParams = new byte[tmpBytesToRead];
//            reader.readFully(AliceParams);
//
//            System.out.println("got params:");
//            System.out.println(toHexString(AliceParams));
//
//            //Create cipher
//            SecretKeySpec bobAesKey = new SecretKeySpec(Bob.getSharedSecret(), 0, 16, "AES");
//            CryptoAESUtil bobCipher = new CryptoAESUtil(bobAesKey, AliceParams, "AES/CBC/PKCS5Padding");


            //Test
           /* tmpBytesToRead = reader.readInt();
            byte[] cipherText = new byte[tmpBytesToRead];
            reader.readFully(cipherText);

            System.out.println("got cipher text:");
            System.out.println(toHexString(cipherText));

            String reovered = new String(bobCipher.decrypt(cipherText));
            writer.writeUTF(reovered);*/

            String username = reader.readUTF();
            SecureRandom sRand = new SecureRandom();
            int sRandInt1 = sRand.nextInt();

            //Sign DH public
            DSASignature DSASigner = new DSASignature();
            byte[] signedPub = DSASigner.sign(Bob.getEncodedPublicKey());

            //send and Get sginature public key
            writer.writeInt(DSASigner.getPublicKey().getEncoded().length);
            writer.write(DSASigner.getPublicKey().getEncoded());
            tmpBytesToRead = reader.readInt();
            byte[] signaturePubKey = new byte[tmpBytesToRead];
            reader.readFully(signaturePubKey);

            //Send signed pub bob and nonce
            writer.writeInt(signedPub.length);
            writer.write(signedPub);
            writer.writeInt(sRandInt1);

            //Send salt
            Account acc = Authenticator.get_account(username);
            writer.writeUTF(acc.getSalt());

            //Get params like IV
            tmpBytesToRead = reader.readInt();
            byte[] AliceParams = new byte[tmpBytesToRead];
            reader.readFully(AliceParams);

            System.out.println("got params:");
            System.out.println(toHexString(AliceParams));

            //Create cipher
            SecretKeySpec bobAesKey = new SecretKeySpec(Bob.getSharedSecret(), 0, 16, "AES");
            CryptoAESUtil bobCipher = new CryptoAESUtil(bobAesKey, AliceParams, "AES/CBC/PKCS5Padding");

            //Rceive {nonce+1|H(PWD)}Ks + nonce2 + signed alice pub
            tmpBytesToRead = reader.readInt();
            byte[] cryptedPWD = new byte[tmpBytesToRead];
            reader.readFully(cryptedPWD);
            byte[] decryptedPWD = bobCipher.decrypt(cryptedPWD);

            int nounce11 = reader.readInt();

            int nonce2 = reader.readInt() + 1;

            tmpBytesToRead = reader.readInt();
            byte[] clientSignature = new byte[tmpBytesToRead];
            reader.readFully(clientSignature);


            boolean isValidSign = DSASignature.checkSignature(signaturePubKey, clientSignature, alicePubKey);

            if (!isValidSign) {
                System.err.println("Invalid signature aborting");
            } else {
                int nonceReceived = nounce11;
                String PWDstr = new String(decryptedPWD);

                boolean isPWDValid = Authenticator.checkPasswordHash(username, PWDstr);
                if (!isPWDValid || (nonceReceived - 1) != sRandInt1)
                    System.err.println("Password/nonce not valid aborting");
                else {
                    String strToken = Authenticator.generateToken(username);
                    acc.setToken(strToken);
                    byte[] token = strToken.getBytes();

                    //byte[] signedToken = DSASigner.sign(token.getBytes());
                    writer.writeInt(token.length);
                    writer.write(token);

                    System.out.println("Does it have permission?");
                    System.out.println(TokenUtil.checkPermission(acc, "Fserver", AccessOperation.READ));
                }

            }


            System.out.println("Connection closed\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getParams() throws IOException {
        //Get params like IV
        int tmpBytesToRead = reader.readInt();
        byte[] AliceParams = new byte[tmpBytesToRead];
        reader.readFully(AliceParams);

        System.out.println("got params:");
        System.out.println(toHexString(AliceParams));
    }
}
