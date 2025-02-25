package com.jwtauth.jwtauth.service;

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
}
