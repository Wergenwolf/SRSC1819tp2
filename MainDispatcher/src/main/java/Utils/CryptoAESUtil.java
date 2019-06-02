package Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;

public class CryptoAESUtil {
    Cipher cipher;
    SecretKeySpec keySpec;
    AlgorithmParameters params;

    public CryptoAESUtil(SecretKeySpec keySpec, String info) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        cipher = Cipher.getInstance(info, "BC");
        this.keySpec = keySpec;
    }

    public CryptoAESUtil(SecretKeySpec keySpec, byte[] encodedParameters, String info) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, NoSuchPaddingException {
        params = AlgorithmParameters.getInstance("AES", "BC");
        params.init(encodedParameters);
        cipher = Cipher.getInstance(info, "BC");
        this.keySpec = keySpec;
    }

    public byte[] encrypt(byte[] clearText) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        if (this.params == null) {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(clearText);

        } else {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, this.params);
            return cipher.doFinal(clearText);
        }
    }

    public byte[] decrypt(byte[] encryptedText) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        if (this.params == null) {
            cipher.init(Cipher.DECRYPT_MODE, this.keySpec);
            return cipher.doFinal(encryptedText);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, this.keySpec, this.params);
            return cipher.doFinal(encryptedText);
        }
    }

    public byte[] getEncodedParameters() throws IOException {
        return cipher.getParameters().getEncoded();
    }
}
