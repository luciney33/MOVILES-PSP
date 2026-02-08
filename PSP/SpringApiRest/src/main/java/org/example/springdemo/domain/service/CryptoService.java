package org.example.springdemo.domain.service;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@Service
public class CryptoService {
    
    private static final String RSA_ALGORITHM = "RSA";
    private static final int RSA_KEY_SIZE = 2048;
    private static final String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 256;
    private static final String AES_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    
    // PBKDF2 parameters
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int PBKDF2_KEY_LENGTH = 256;
    
    /**
     * Generate RSA key pair (2048 bits)
     */
    public KeyPair generarParClaves() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }
    
    /**
     * Encrypt private key with AES derived from password using PBKDF2
     */
    public byte[] cifrarClavePrivada(PrivateKey privateKey, String password) 
            throws NoSuchAlgorithmException, InvalidKeySpecException, 
                   NoSuchPaddingException, InvalidKeyException, 
                   InvalidAlgorithmParameterException, IllegalBlockSizeException, 
                   BadPaddingException {
        // Generate salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        // Derive AES key from password using PBKDF2
        SecretKey aesKey = deriveKeyFromPassword(password, salt);
        
        // Generate IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        random.nextBytes(iv);
        
        // Encrypt private key
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmParameterSpec);
        byte[] encryptedKey = cipher.doFinal(privateKey.getEncoded());
        
        // Concatenate salt + iv + encrypted key
        byte[] result = new byte[salt.length + iv.length + encryptedKey.length];
        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(iv, 0, result, salt.length, iv.length);
        System.arraycopy(encryptedKey, 0, result, salt.length + iv.length, encryptedKey.length);
        
        return result;
    }
    
    /**
     * Decrypt private key using password
     */
    public PrivateKey descifrarClavePrivada(byte[] encryptedData, String password) 
            throws NoSuchAlgorithmException, InvalidKeySpecException, 
                   NoSuchPaddingException, InvalidKeyException, 
                   InvalidAlgorithmParameterException, IllegalBlockSizeException, 
                   BadPaddingException {
        // Extract salt, iv, and encrypted key
        byte[] salt = Arrays.copyOfRange(encryptedData, 0, 16);
        byte[] iv = Arrays.copyOfRange(encryptedData, 16, 16 + GCM_IV_LENGTH);
        byte[] encryptedKey = Arrays.copyOfRange(encryptedData, 16 + GCM_IV_LENGTH, encryptedData.length);
        
        // Derive AES key from password
        SecretKey aesKey = deriveKeyFromPassword(password, salt);
        
        // Decrypt private key
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmParameterSpec);
        byte[] decryptedKey = cipher.doFinal(encryptedKey);
        
        // Convert to PrivateKey
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * Derive AES key from password using PBKDF2
     */
    private SecretKey deriveKeyFromPassword(String password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
    
    /**
     * Generate random AES key (256 bits)
     */
    public SecretKey generarClaveAES() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }
    
    /**
     * Encrypt content with AES/GCM
     */
    public byte[] cifrarConAES(String content, SecretKey aesKey) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, 
                   InvalidAlgorithmParameterException, InvalidKeyException, 
                   IllegalBlockSizeException, BadPaddingException {
        // Generate IV
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[GCM_IV_LENGTH];
        random.nextBytes(iv);
        
        // Encrypt content
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmParameterSpec);
        byte[] encryptedContent = cipher.doFinal(content.getBytes());
        
        // Concatenate iv + encrypted content
        byte[] result = new byte[iv.length + encryptedContent.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedContent, 0, result, iv.length, encryptedContent.length);
        
        return result;
    }
    
    /**
     * Decrypt content with AES/GCM
     */
    public String descifrarConAES(byte[] encryptedData, SecretKey aesKey) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, 
                   InvalidAlgorithmParameterException, InvalidKeyException, 
                   IllegalBlockSizeException, BadPaddingException {
        // Extract IV and encrypted content
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
        byte[] encryptedContent = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedData.length);
        
        // Decrypt content
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmParameterSpec);
        byte[] decryptedContent = cipher.doFinal(encryptedContent);
        
        return new String(decryptedContent);
    }
    
    /**
     * Encrypt AES key with RSA public key
     */
    public byte[] cifrarClaveAESConRSA(SecretKey aesKey, PublicKey publicKey) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, 
                   InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }
    
    /**
     * Decrypt AES key with RSA private key
     */
    public SecretKey descifrarClaveAESConRSA(byte[] encryptedAesKey, PrivateKey privateKey) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, 
                   InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedAesKey);
        return new SecretKeySpec(decryptedKeyBytes, AES_ALGORITHM);
    }
    
    /**
     * Sign data with private key using SHA256withRSA
     */
    public byte[] firmar(byte[] data, PrivateKey privateKey) 
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }
    
    /**
     * Verify signature with public key
     */
    public boolean verificarFirma(byte[] data, byte[] signatureBytes, PublicKey publicKey) 
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
    
    /**
     * Convert byte array to PublicKey
     */
    public PublicKey byteArrayToPublicKey(byte[] publicKeyBytes) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }
}
