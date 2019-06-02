package Utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class CryptoUtil {

    //AES
    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[]{'F', 'C', 'T', '/', 'U', 'N', 'L', 'r', 'o', 'c', 'k', 's', '!', '!', 'd', 'i'};
    private static final Key key = new SecretKeySpec(keyValue, ALGO);

    /**
     * Encrypts a String value
     *
     * @param Data A String value to encrypt
     * @return A String encrypted
     * @throws Exception
     */
    public static String encryptAES(String Data) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        return java.util.Base64.getEncoder().encodeToString(encVal);
    }

    /**
     * Decrypts a String value
     *
     * @param encrypted Encrypted String to decrypt
     * @return Decrypted String
     * @throws Exception
     */
    public static String decryptAES(String encrypted) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = java.util.Base64.getDecoder().decode(encrypted);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }

    //bCrypt
    public static String bCryptEncrypt(String password, String salt) {
        return BCrypt.hashpw(password, salt);
    }

    public static boolean doesPasswordMatch(String candidate_password, String stored_hash) {
        return BCrypt.checkpw(candidate_password, stored_hash);
    }

    /**
     * The genSalt() method takes an optional parameter (log_rounds)
     * that determines the computational complexity of the hashing:
     * <p>
     * The amount of work increases exponentially (2**log_rounds),
     * so each increment is twice as much work. The default log_rounds is 10.
     *
     * @param log_rounds Determines the computational complexity of the hashing.
     *                   (valid range is 4 to 31)
     * @return String - Returns the generated Salt
     */
    public static String genSalt(int log_rounds) {
        return BCrypt.gensalt(log_rounds);
    }


}
