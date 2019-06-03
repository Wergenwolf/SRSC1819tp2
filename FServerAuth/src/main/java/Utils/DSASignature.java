package Utils;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DSASignature {

    private Signature signature;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public DSASignature() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "BC");

        keyGen.initialize(2048, new SecureRandom());

        KeyPair keyPair = keyGen.generateKeyPair();
        signature = Signature.getInstance("SHA256withDSA", "BC");

        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public static boolean checkSignature(PublicKey pubKey, byte[] signedMessage, byte[] message) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
        //Verificar - neste caso estamos a obter a chave publica do par mas
        // em geral usamos a chave publica que previamente conhecemos de
        // quem assinou.
        //
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Signature signature = Signature.getInstance("SHA256withDSA", "BC");

        signature.initVerify(pubKey);
        signature.update(message);

        return signature.verify(signedMessage);


    }

    public static boolean checkSignature(byte[] pubKey, byte[] signedMessage, byte[] message) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKey);

        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "BC");

        return checkSignature(keyFactory.generatePublic(pubKeySpec), signedMessage, message);

    }

    public static void main(
            String[] args)
            throws Exception {
        byte[] message = new byte[]
                {(byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd'};

        DSASignature DSASign = new DSASignature();

        byte[] signedMSG = DSASign.sign(message);


        boolean isValid = checkSignature(DSASign.getPublicKey(), signedMSG, message);
        if (isValid) {
            System.out.println("Assinatura validada - reconhecida");
        } else {
            System.out.println("Assinatura nao reconhecida");
        }

    }

    public byte[] sign(byte[] message) throws InvalidKeyException, SignatureException {
        signature.initSign(this.privateKey);
        this.signature.update(message);
        byte[] sigBytes = this.signature.sign();

        return sigBytes;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}
