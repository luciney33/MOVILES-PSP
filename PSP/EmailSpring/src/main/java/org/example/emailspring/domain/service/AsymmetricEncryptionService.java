package org.example.emailspring.domain.service;

import org.example.emailspring.common.Constantes;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class AsymmetricEncryptionService {

    public KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(Constantes.CIPHER_RSA_ALGORITHM);
        kpg.initialize(Constantes.CIPHER_RSA_KEY_SIZE);
        return kpg.generateKeyPair();
    }


    public PublicKey bytesToPublicKey(byte[] bytes) throws Exception {
        return KeyFactory.getInstance(Constantes.CIPHER_RSA_ALGORITHM).generatePublic(new X509EncodedKeySpec(bytes));
    }

    public PrivateKey bytesToPrivateKey(byte[] bytes) throws Exception {
        return KeyFactory.getInstance(Constantes.CIPHER_RSA_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    public byte[] encryptKey(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(Constantes.CIPHER_RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }

    public byte[] decryptKey(byte[] encryptedKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(Constantes.CIPHER_RSA_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedKey);
    }

    public byte[] firma(byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance(Constantes.CIPHER_RSA_SIGNATURE_ALGORITHM);
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public boolean verify(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance(Constantes.CIPHER_RSA_SIGNATURE_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }
}

