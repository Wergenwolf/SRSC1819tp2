import Utils.CryptoAESUtil;
import Utils.CryptoUtil;
import Utils.DSASignature;
import Utils.DiffieHellman;
import book.SSLClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class Client {
    //Comands
    private static final String EXIT = "SAIR";
    private static final String LOGIN = "LOGIN";

    //Messages
    private static final String INVALID_COMMAND = "Commando invalido.";

    private static DataInputStream reader;
    private static DataOutputStream writer;
    private static SSLClient client;
    private static CryptoAESUtil AliceCipher;

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


        client = new SSLClient("localhost",
                8123,
                "Client/client/client.properties",
                "987654".toCharArray());

        try {
            reader = new DataInputStream(client.getInputStream());
            writer = new DataOutputStream(client.getOutputStream());


            Scanner in = new Scanner(System.in);
            String command = "";
            while (!command.equals(EXIT)) {
                command = in.nextLine().toUpperCase();
                commandsAnalyzer(command, in);
            }


        } catch (IOException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchProviderException | NoSuchAlgorithmException | SignatureException e) {
            e.printStackTrace();
        }
    }

    /**
     * Analiza o comando inserido pelo utilizador.
     *
     * @param command - Comando inserido pelo utilizador.
     */
    public static void commandsAnalyzer(String command, Scanner in) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, SignatureException {
        String[] splited = command.split("\\s+");
        switch (splited[0]) {
            case LOGIN:
                if (splited.length != 3) {
                    System.out.println(INVALID_COMMAND);
                    break;
                }
                login(splited[1], splited[2]);
                break;
            default:
                System.out.println(INVALID_COMMAND);
                break;
        }
    }


    private static void close() throws IOException {
        reader.close();
        writer.close();
        client.close();
    }

    private static void login(String username, String password) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, SignatureException {
        int tmpBytesToRead;


        writer.writeUTF("login");

        DiffieHellman Alice = new DiffieHellman(2048);//Inicialize Alice
        //Send Bob public key
        System.out.println("SENT: " + Alice.getEncodedPublicKey().length);
        byte[] pubKey = Alice.getEncodedPublicKey();
        int ll = Alice.getEncodedPublicKey().length;
        writer.writeInt(Alice.getEncodedPublicKey().length);
        writer.write(Alice.getEncodedPublicKey(), 0, Alice.getEncodedPublicKey().length);

        //Get Bob's public key
        tmpBytesToRead = reader.readInt();
        byte[] BobPublicKey = new byte[tmpBytesToRead];
        reader.readFully(BobPublicKey);

        //Do DH
        Alice.instantiateDHPublicKey(BobPublicKey); //Inicialize with Bob's key
        Alice.doPhase(); //Do calculations

        //Create a symmetric AES CBC cipher with PKCS5Padding
        SecretKeySpec aliceAesKey = new SecretKeySpec(Alice.getSharedSecret(), 0, 16, "AES");
        AliceCipher = new CryptoAESUtil(aliceAesKey, "AES/CBC/PKCS5Padding");

        //TEST
        // byte[] cleartext = "This is just an example".getBytes();
        // byte[] ciphertext = AliceCipher.encrypt(cleartext);

        //Send Bob parameters like IV so he can decode/encode.
       /* byte[] params = AliceCipher.getEncodedParameters();
        System.out.println("Sent params:");
        System.out.println(toHexString(params));
        writer.writeInt(params.length);
        writer.write(params);

        System.out.println("Sent cipherTExt:");
        System.out.println(toHexString(ciphertext));
        writer.writeInt(ciphertext.length);
        writer.write(ciphertext);

        String recored = reader.readUTF();
        System.out.println(recored);*/

        writer.writeUTF(username);

        //Sign DH public
        DSASignature DSASigner = new DSASignature();
        byte[] signedPub = DSASigner.sign(Alice.getEncodedPublicKey());

        //get and send sginature public key
        tmpBytesToRead = reader.readInt();
        byte[] signedPublicKey = new byte[tmpBytesToRead];
        reader.readFully(signedPublicKey);
        writer.writeInt(DSASigner.getPublicKey().getEncoded().length);
        writer.write(DSASigner.getPublicKey().getEncoded());

        //receivei nonce
        tmpBytesToRead = reader.readInt();
        byte[] signedBobPubkey = new byte[tmpBytesToRead];
        reader.readFully(signedBobPubkey);
        int nonce = reader.readInt() + 1;

        String salt = reader.readUTF();

        String PWD = CryptoUtil.bCryptEncrypt(password, salt);
        byte[] PWDb = PWD.getBytes();
        byte[] sendPWD = AliceCipher.encrypt(PWDb);

        byte[] params = AliceCipher.getEncodedParameters();
        System.out.println("Sent params:");
        System.out.println(toHexString(params));
        writer.writeInt(params.length);
        writer.write(params);

        writer.writeInt(sendPWD.length);
        writer.write(sendPWD);

        writer.writeInt(nonce);

        SecureRandom sRand = new SecureRandom();
        int sRandInt1 = sRand.nextInt();
        writer.writeInt(sRandInt1);

        byte[] signedAlicePubKey = DSASigner.sign(Alice.getEncodedPublicKey());
        writer.writeInt(signedAlicePubKey.length);
        writer.write(signedAlicePubKey);


        tmpBytesToRead = reader.readInt();
        byte[] cryptedToken = new byte[tmpBytesToRead];
        reader.readFully(cryptedToken);

        byte[] clearToken = AliceCipher.decrypt(cryptedToken);
        String strClearToken = new String(clearToken);
        System.out.println(strClearToken);
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
}
