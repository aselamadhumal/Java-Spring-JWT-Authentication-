package com.jwtauth.jwtauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;



@Service
public class EncDecService {

    private static final String RSA = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    @Value("${private.key}")
    private String privateKeyValue;

    @Value("${public.key}")
    private String publicKeyValue;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            if (privateKeyValue == null || publicKeyValue == null) {
                throw new IllegalStateException("Private or public key is not configured.");
            }
            privateKey = getPrivateKeyFromString(privateKeyValue);
            publicKey = getPublicKeyFromString(publicKeyValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize keys: " + e.getMessage(), e);
        }
    }


    private PrivateKey getPrivateKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(spec);
    }

    private PublicKey getPublicKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(spec);
    }

    public String encrypt(String data) throws Exception {
        init();  // Ensure keys are initialized
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedValue = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    public String decrypt(String encryptedData) throws Exception {
        init();  // Ensure keys are initialized
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedValue = cipher.doFinal(Base64.getMimeDecoder().decode(encryptedData));
        return new String(decryptedValue);
    }


}

//when key is not that private and public key is auto generate
/*package com.jwtauth.jwtauth.service;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.security.*;
import java.util.Base64;

@Service
public class EncDecService {

    private static final String RSA = "RSA";
    private PrivateKey privateKey;
    private PublicKey publicKey;

    // Initialize the key pair only once
    public void init() throws NoSuchAlgorithmException {
        if (privateKey == null || publicKey == null) {
            KeyPair keyPair = generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(4096);  // You may choose a different key size if needed
        return keyPairGenerator.generateKeyPair();
    }

    // Encrypt the data using the public key
    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        init();  // Ensure keys are initialized
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedValue = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    // Decrypt the data using the private key
    public String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        init();  // Ensure keys are initialized
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedValue = cipher.doFinal(Base64.getMimeDecoder().decode(encryptedData));
        return new String(decryptedValue);
    }
}*/
