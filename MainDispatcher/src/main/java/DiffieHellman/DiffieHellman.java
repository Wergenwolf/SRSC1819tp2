package DiffieHellman;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DiffieHellman {

    private KeyPairGenerator keyPairGenerator;
    private KeyPair KPair;
    private KeyAgreement keyAgreement;

    private PublicKey otherPublicKey;

    public DiffieHellman(int keySize) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        initializeKeyPairGenerator(keySize);
        generateKeyPair(this.keyPairGenerator);
        initializeDHKeyAgreement();
    }

    public DiffieHellman(byte[] encodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchProviderException {
        /*
         * He instantiates a DH public key from the encoded key material.
         */
        instantiateDHPublicKey(encodedPublicKey);

        /*
         * Bob gets the DH parameters associated with Alice's public key.
         * He must use the same parameters when he generates his own key
         * pair.
         */
        DHParameterSpec dhParamFromPubKey = ((DHPublicKey) this.otherPublicKey).getParams();

        // Bob creates his own DH key pair
        System.out.println("Generate DH keypair ...");
        keyPairGenerator = KeyPairGenerator.getInstance("DH", "BC");
        keyPairGenerator.initialize(dhParamFromPubKey);
        generateKeyPair(keyPairGenerator);

        // Bob creates and initializes his DH KeyAgreement object
        initializeDHKeyAgreement();
    }

    private void initializeKeyPairGenerator(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {

        //Create a generator with 'keySize' bit key size
        System.out.println("Generate DH keypair ...");
        keyPairGenerator = KeyPairGenerator.getInstance("DH", "BC");
        keyPairGenerator.initialize(keySize);
    }

    public void generateKeyPair(KeyPairGenerator keyPairGenerator) {
        this.KPair = keyPairGenerator.generateKeyPair();
    }

    private void initializeDHKeyAgreement() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        // Alice creates and initializes her DH KeyAgreement object
        System.out.println("Initialization ...");
        this.keyAgreement = KeyAgreement.getInstance("DH", "BC");
        this.keyAgreement.init(this.KPair.getPrivate());
    }

    public byte[] getEncodedPublicKey() {
        //Encoded pub key to send
        return KPair.getPublic().getEncoded();
    }

    public KeyAgreement getKeyAgreement() {
        return this.keyAgreement;
    }

    public void instantiateDHPublicKey(byte[] encodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException {
        /*
         * Alice uses Bob's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from Bob's encoded key material.
         */

        KeyFactory KeyFac = KeyFactory.getInstance("DH", "BC");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(encodedPublicKey);
        this.otherPublicKey = KeyFac.generatePublic(x509KeySpec);

    }

    public void doPhase() throws InvalidKeyException {
        System.out.println("Execute PHASE1 ...");
        keyAgreement.doPhase(this.otherPublicKey, true);
    }


    public byte[] getSharedSecret() {
        return keyAgreement.generateSecret();
    }

    public SecretKeySpec generateAESSecretKey() {
        return new SecretKeySpec(getSharedSecret(), 0, 16, "AES");
    }
}
