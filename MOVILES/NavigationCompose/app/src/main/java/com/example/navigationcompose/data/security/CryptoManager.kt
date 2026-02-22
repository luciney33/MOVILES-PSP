package com.example.navigationcompose.data.security

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.navigationcompose.common.Constantes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

private val Context.cryptoDataStore by preferencesDataStore(name = Constantes.DATASTORE_CRYPTO_NAME)

@Singleton
class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val KEY_ENCRYPTED_PRIVATE_KEY = byteArrayPreferencesKey(Constantes.KEY_ENCRYPTED_PRIVATE_KEY)
        private val KEY_SALT = byteArrayPreferencesKey(Constantes.KEY_SALT)
        private val KEY_IV_PRIVATE_KEY = byteArrayPreferencesKey(Constantes.KEY_IV_PRIVATE_KEY)
        private val KEY_PUBLIC_KEY = byteArrayPreferencesKey(Constantes.KEY_PUBLIC_KEY)
        private val KEY_CERTIFICADO = stringPreferencesKey(Constantes.KEY_CERTIFICADO)
    }

    fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(Constantes.RSA_ALGORITHM).apply {
            initialize(Constantes.RSA_KEY_SIZE, SecureRandom())
        }
        return keyPairGenerator.generateKeyPair()
    }

    fun generateAESKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(Constantes.AES_ALGORITHM).apply {
            init(Constantes.AES_KEY_SIZE, SecureRandom())
        }
        return keyGenerator.generateKey()
    }

    fun generateSalt(): ByteArray {
        return ByteArray(Constantes.SALT_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
    }

    fun generateIV(): ByteArray {
        return ByteArray(Constantes.IV_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
    }

    fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            Constantes.PBKDF2_ITERATIONS,
            Constantes.AES_KEY_SIZE
        )
        val factory = SecretKeyFactory.getInstance(Constantes.PBKDF2_ALGORITHM)
        val derivedKeyBytes = factory.generateSecret(spec).encoded

        spec.clearPassword()

        return SecretKeySpec(derivedKeyBytes, Constantes.AES_ALGORITHM)
    }

    fun encryptAES_GCM(data: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(Constantes.AES_CIPHER)
        val spec = GCMParameterSpec(Constantes.GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        return cipher.doFinal(data)
    }

    fun decryptAES_GCM(encryptedData: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(Constantes.AES_CIPHER)
        val spec = GCMParameterSpec(Constantes.GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encryptedData)
    }
    fun encryptRSA(data: ByteArray, publicKey: PublicKey): ByteArray {
        if (data.size > Constantes.RSA_4096_MAX_BYTES) {
            throw IllegalArgumentException(Constantes.ERROR_DATOS_GRANDES_RSA)
        }
        val cipher = Cipher.getInstance(Constantes.RSA_CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    fun decryptRSA(encryptedData: ByteArray, privateKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance(Constantes.RSA_CIPHER)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(encryptedData)
    }

    fun signData(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance(Constantes.SIGNATURE_ALGORITHM)
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun verifySignature(data: ByteArray, signatureBytes: ByteArray, publicKey: PublicKey): Boolean {
        return try {
            val signature = Signature.getInstance(Constantes.SIGNATURE_ALGORITHM)
            signature.initVerify(publicKey)
            signature.update(data)
            signature.verify(signatureBytes)
        } catch (_: Exception) {
            false
        }
    }

    fun bytesToPublicKey(bytes: ByteArray): PublicKey {
        val spec = X509EncodedKeySpec(bytes)
        val factory = KeyFactory.getInstance(Constantes.RSA_ALGORITHM)
        return factory.generatePublic(spec)
    }

    fun bytesToPrivateKey(bytes: ByteArray): PrivateKey {
        val spec = PKCS8EncodedKeySpec(bytes)
        val factory = KeyFactory.getInstance(Constantes.RSA_ALGORITHM)
        return factory.generatePrivate(spec)
    }

    fun bytesToAESKey(bytes: ByteArray): SecretKey {
        return SecretKeySpec(bytes, Constantes.AES_ALGORITHM)
    }

    suspend fun saveEncryptedKeys(
        encryptedPrivateKey: ByteArray,
        salt: ByteArray,
        ivPrivateKey: ByteArray,
        publicKey: ByteArray
    ) {
        this@CryptoManager.context.cryptoDataStore.edit { preferences ->
            preferences[KEY_ENCRYPTED_PRIVATE_KEY] = encryptedPrivateKey
            preferences[KEY_SALT] = salt
            preferences[KEY_IV_PRIVATE_KEY] = ivPrivateKey
            preferences[KEY_PUBLIC_KEY] = publicKey
        }
    }

    suspend fun savePublicKeyOnly(publicKey: ByteArray) {
        this@CryptoManager.context.cryptoDataStore.edit { preferences ->
            preferences[KEY_PUBLIC_KEY] = publicKey
        }
    }

    suspend fun saveCertificado(certificadoBase64: String) {
        this@CryptoManager.context.cryptoDataStore.edit { preferences ->
            preferences[KEY_CERTIFICADO] = certificadoBase64
        }
    }

    suspend fun loadAndDecryptPrivateKey(password: String): PrivateKey {
        val preferences = this@CryptoManager.context.cryptoDataStore.data.first()

        val encryptedPrivateKey = preferences[KEY_ENCRYPTED_PRIVATE_KEY]
            ?: throw IllegalStateException(Constantes.ERROR_NO_CLAVE_PRIVADA)
        val salt = preferences[KEY_SALT]
            ?: throw IllegalStateException(Constantes.ERROR_NO_SALT)
        val iv = preferences[KEY_IV_PRIVATE_KEY]
            ?: throw IllegalStateException(Constantes.ERROR_NO_IV)

        val derivedKey = deriveKeyFromPassword(password, salt)

        val privateKeyBytes = decryptAES_GCM(encryptedPrivateKey, derivedKey, iv)

        return bytesToPrivateKey(privateKeyBytes)
    }

    suspend fun getPublicKey(): PublicKey {
        val preferences = this@CryptoManager.context.cryptoDataStore.data.first()
        val publicKeyBytes = preferences[KEY_PUBLIC_KEY]
            ?: throw IllegalStateException(Constantes.ERROR_NO_CLAVE_PUBLICA)
        return bytesToPublicKey(publicKeyBytes)
    }

    suspend fun getPublicKeyBase64(): String {
        val publicKey = getPublicKey()
        return Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
    }

    suspend fun getCertificado(): String? {
        return this@CryptoManager.context.cryptoDataStore.data.map { preferences ->
            preferences[KEY_CERTIFICADO]
        }.first()
    }

    suspend fun hasStoredKeys(): Boolean {
        val preferences = this@CryptoManager.context.cryptoDataStore.data.first()
        return preferences[KEY_ENCRYPTED_PRIVATE_KEY] != null &&
               preferences[KEY_PUBLIC_KEY] != null
    }

    suspend fun clearAllKeys() {
        this@CryptoManager.context.cryptoDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun sha256(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance(Constantes.SHA256_ALGORITHM)
        return digest.digest(data)
    }
}

