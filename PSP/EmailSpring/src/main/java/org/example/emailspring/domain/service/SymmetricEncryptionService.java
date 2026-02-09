package org.example.emailspring.domain.service;

import org.example.emailspring.common.Constantes;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;


@Service
public class SymmetricEncryptionService {

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(Constantes.CIPHER_AES_ALGORITHM);
        kg.init(Constantes.CIPHER_AES_KEY_SIZE);
        return kg.generateKey();
    }


    public SecretKey generateKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(Constantes.CIPHER_PBKDF2_ALGORITHM);
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                Constantes.CIPHER_PBKDF2_ITERATIONS,
                Constantes.CIPHER_AES_KEY_SIZE
        );
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), Constantes.CIPHER_AES_ALGORITHM);
    }

    public byte[] generateSalt() {
        byte[] salt = new byte[Constantes.CIPHER_SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public byte[] generateIV() {
        byte[] iv = new byte[Constantes.CIPHER_GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public byte[] encrypt(byte[] plainData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(Constantes.CIPHER_AES_GCM_TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(Constantes.CIPHER_GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
        return cipher.doFinal(plainData);
    }


    public byte[] decrypt(byte[] encryptedData, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(Constantes.CIPHER_AES_GCM_TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(Constantes.CIPHER_GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
        return cipher.doFinal(encryptedData);
    }

    public SecretKey bytesToKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, Constantes.CIPHER_AES_ALGORITHM);
    }

}

