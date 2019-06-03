import Utils.CryptoAESUtil;
import Utils.DiffieHellman.DiffieHellman;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class DispatcherThread extends Thread {
    private static DataInputStream reader;
    private static DataOutputStream writer;
    private Socket socket;

    public DispatcherThread(Socket clientSocket) {
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
            tmpBytesToRead = reader.readInt();
            byte[] AliceParams = new byte[tmpBytesToRead];
            reader.readFully(AliceParams);

            System.out.println("got params:");
            System.out.println(toHexString(AliceParams));

            //Create cipher
            SecretKeySpec bobAesKey = new SecretKeySpec(Bob.getSharedSecret(), 0, 16, "AES");
            CryptoAESUtil bobCipher = new CryptoAESUtil(bobAesKey, AliceParams, "AES/CBC/PKCS5Padding");


            //Test
//            tmpBytesToRead = reader.readInt();
//            byte[] cipherText = new byte[tmpBytesToRead];
//            reader.readFully(cipherText);
//
//            System.out.println("got cipher text:");
//            System.out.println(toHexString(cipherText));
//
//            String reovered = new String(bobCipher.decrypt(cipherText));
//            writer.writeUTF(reovered);

            String username = reader.readUTF();
            SecureRandom sRand = new SecureRandom();
            int sRandInt1 = sRand.nextInt();


            System.out.println("Connection closed\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }
}
