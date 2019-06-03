package Utils;


import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class DHTestMain {

    public static void main(String[] argv) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        DiffieHellman Alice;
        DiffieHellman Bob;

        {
            try {
                Alice = new DiffieHellman(2048); //Inicialize Alice

                Bob = new DiffieHellman(Alice.getEncodedPublicKey()); //Inicialize Bob with Alice's pubKey
                Bob.doPhase(); //Bob does the calculations
                Alice.instantiateDHPublicKey(Bob.getEncodedPublicKey()); //give Alice bob's public key
                Alice.doPhase();//Alice does the calculations

                byte[] aliceSharedSecret = Alice.getKeyAgreement().generateSecret();
                int aliceLen = aliceSharedSecret.length;
                byte[] bobSharedSecret = new byte[aliceLen];
                int bobLen = Bob.getKeyAgreement().generateSecret(bobSharedSecret, 0);

                System.out.println("Alice secret: " +
                        toHexString(aliceSharedSecret));
                System.out.println("Bob secret: " +
                        toHexString(bobSharedSecret));

                if (!java.util.Arrays.equals(aliceSharedSecret, bobSharedSecret))
                    throw new Exception("Shared secrets differ");
                System.out.println("Shared secrets are the same");

                System.out.println("Use shared secret as SecretKey object ...");
                SecretKeySpec bobAesKey = new SecretKeySpec(bobSharedSecret, 0, 16, "AES");
                SecretKeySpec aliceAesKey = new SecretKeySpec(aliceSharedSecret, 0, 16, "AES");

                CryptoAESUtil bobCipher = new CryptoAESUtil(bobAesKey, "AES/CBC/PKCS5Padding");

                byte[] cleartext = "This is just an example".getBytes();
                byte[] ciphertext = bobCipher.encrypt(cleartext);

                CryptoAESUtil aliceCipher = new CryptoAESUtil(aliceAesKey, bobCipher.getEncodedParameters(), "AES/CBC/PKCS5Padding");

                byte[] recovered = aliceCipher.decrypt(ciphertext);

                if (!java.util.Arrays.equals(cleartext, recovered))
                    throw new Exception("AES in CBC mode recovered text is " +
                            "different from cleartext");
                System.out.println("AES in CBC mode recovered text is same as cleartext");

                System.out.println(new String(cleartext));
                System.out.println(new String(ciphertext));
                System.out.println(new String(recovered));

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
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
