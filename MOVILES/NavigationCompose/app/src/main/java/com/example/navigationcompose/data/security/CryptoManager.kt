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

// Extension para DataStore
private val Context.cryptoDataStore by preferencesDataStore(name = Constantes.DATASTORE_CRYPTO_NAME)

/**
 * CryptoManager: Gestiona todas las operaciones criptográficas del cliente.
 *
 * Responsabilidades:
 * - Generar pares de claves RSA (4096 bits)
 * - Cifrar/descifrar clave privada con PBKDF2 + AES-GCM
 * - Cifrar/descifrar contenido con AES-256-GCM
 * - Firmar y verificar firmas digitales
 * - Gestionar almacenamiento seguro en DataStore
 *
 * IMPORTANTE: La clave privada NUNCA sale del dispositivo sin cifrar.
 */
@Singleton
class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ==================== CONSTANTES ====================

    companion object {
        // DataStore Keys
        private val KEY_ENCRYPTED_PRIVATE_KEY = byteArrayPreferencesKey(Constantes.KEY_ENCRYPTED_PRIVATE_KEY)
        private val KEY_SALT = byteArrayPreferencesKey(Constantes.KEY_SALT)
        private val KEY_IV_PRIVATE_KEY = byteArrayPreferencesKey(Constantes.KEY_IV_PRIVATE_KEY)
        private val KEY_PUBLIC_KEY = byteArrayPreferencesKey(Constantes.KEY_PUBLIC_KEY)
        private val KEY_CERTIFICADO = stringPreferencesKey(Constantes.KEY_CERTIFICADO)
    }

    // ==================== GENERACIÓN DE CLAVES ====================

    /**
     * Genera un nuevo par de claves RSA de 4096 bits.
     * Este proceso puede tardar 2-3 segundos en dispositivos antiguos.
     */
    fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(Constantes.RSA_ALGORITHM).apply {
            initialize(Constantes.RSA_KEY_SIZE, SecureRandom())
        }
        return keyPairGenerator.generateKeyPair()
    }

    /**
     * Genera una clave AES-256 para cifrado simétrico de contenido.
     */
    fun generateAESKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(Constantes.AES_ALGORITHM).apply {
            init(Constantes.AES_KEY_SIZE, SecureRandom())
        }
        return keyGenerator.generateKey()
    }

    /**
     * Genera un salt aleatorio para PBKDF2 (256 bits).
     */
    fun generateSalt(): ByteArray {
        return ByteArray(Constantes.SALT_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
    }

    /**
     * Genera un IV (Initialization Vector) para AES-GCM (96 bits).
     */
    fun generateIV(): ByteArray {
        return ByteArray(Constantes.IV_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
    }

    // ==================== PBKDF2 ====================

    /**
     * Deriva una clave AES-256 desde una contraseña usando PBKDF2.
     *
     * Parámetros según OWASP 2024:
     * - Iteraciones: 100,000 (mínimo recomendado)
     * - Salt: 256 bits
     * - Output: 256 bits (AES-256)
     */
    fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            Constantes.PBKDF2_ITERATIONS,
            Constantes.AES_KEY_SIZE
        )
        val factory = SecretKeyFactory.getInstance(Constantes.PBKDF2_ALGORITHM)
        val derivedKeyBytes = factory.generateSecret(spec).encoded

        // Limpiar contraseña de memoria
        spec.clearPassword()

        return SecretKeySpec(derivedKeyBytes, Constantes.AES_ALGORITHM)
    }

    // ==================== CIFRADO SIMÉTRICO (AES-GCM) ====================

    /**
     * Cifra datos con AES-256-GCM.
     * AES-GCM proporciona cifrado autenticado (confidencialidad + integridad).
     */
    fun encryptAES_GCM(data: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(Constantes.AES_CIPHER)
        val spec = GCMParameterSpec(Constantes.GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        return cipher.doFinal(data)
    }

    /**
     * Descifra datos con AES-256-GCM.
     * Lanza excepción si el tag de autenticación no coincide (datos manipulados).
     */
    fun decryptAES_GCM(encryptedData: ByteArray, key: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(Constantes.AES_CIPHER)
        val spec = GCMParameterSpec(Constantes.GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encryptedData)
    }

    // ==================== CIFRADO ASIMÉTRICO (RSA) ====================

    /**
     * Cifra datos con RSA usando OAEP padding (recomendado sobre PKCS1).
     * Límite: 446 bytes para RSA-4096 con SHA-256.
     * Úsalo para cifrar claves AES, no contenido grande.
     */
    fun encryptRSA(data: ByteArray, publicKey: PublicKey): ByteArray {
        if (data.size > Constantes.RSA_4096_MAX_BYTES) {
            throw IllegalArgumentException(Constantes.ERROR_DATOS_GRANDES_RSA)
        }
        val cipher = Cipher.getInstance(Constantes.RSA_CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    /**
     * Descifra datos con RSA.
     */
    fun decryptRSA(encryptedData: ByteArray, privateKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance(Constantes.RSA_CIPHER)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(encryptedData)
    }

    // ==================== FIRMAS DIGITALES ====================

    /**
     * Firma datos con la clave privada RSA.
     * La firma demuestra que los datos provienen del poseedor de la clave privada.
     */
    fun signData(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance(Constantes.SIGNATURE_ALGORITHM)
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    /**
     * Verifica la firma digital con la clave pública RSA.
     * Retorna true si la firma es válida y los datos no han sido modificados.
     */
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

    // ==================== CONVERSIONES ====================

    /**
     * Convierte bytes a PublicKey RSA.
     */
    fun bytesToPublicKey(bytes: ByteArray): PublicKey {
        val spec = X509EncodedKeySpec(bytes)
        val factory = KeyFactory.getInstance(Constantes.RSA_ALGORITHM)
        return factory.generatePublic(spec)
    }

    /**
     * Convierte bytes a PrivateKey RSA.
     */
    fun bytesToPrivateKey(bytes: ByteArray): PrivateKey {
        val spec = PKCS8EncodedKeySpec(bytes)
        val factory = KeyFactory.getInstance(Constantes.RSA_ALGORITHM)
        return factory.generatePrivate(spec)
    }

    /**
     * Convierte bytes a SecretKey AES.
     */
    fun bytesToAESKey(bytes: ByteArray): SecretKey {
        return SecretKeySpec(bytes, Constantes.AES_ALGORITHM)
    }

    // ==================== ALMACENAMIENTO SEGURO (DataStore) ====================

    /**
     * Guarda las claves en DataStore de forma segura.
     * - Clave privada: CIFRADA con contraseña
     * - Clave pública: En claro
     * - Salt e IV: Necesarios para descifrar la privada
     */
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

    /**
     * Guarda el certificado del servidor (firma de la clave pública).
     */
    suspend fun saveCertificado(certificadoBase64: String) {
        this@CryptoManager.context.cryptoDataStore.edit { preferences ->
            preferences[KEY_CERTIFICADO] = certificadoBase64
        }
    }

    /**
     * Carga y descifra la clave privada usando la contraseña del usuario.
     *
     * @throws IllegalStateException si no hay claves guardadas
     * @throws javax.crypto.AEADBadTagException si la contraseña es incorrecta
     */
    suspend fun loadAndDecryptPrivateKey(password: String): PrivateKey {
        val preferences = this@CryptoManager.context.cryptoDataStore.data.first()

        val encryptedPrivateKey = preferences[KEY_ENCRYPTED_PRIVATE_KEY]
            ?: throw IllegalStateException("No hay clave privada guardada")
        val salt = preferences[KEY_SALT]
            ?: throw IllegalStateException("No hay salt guardado")
        val iv = preferences[KEY_IV_PRIVATE_KEY]
            ?: throw IllegalStateException("No hay IV guardado")

        // Derivar clave de la contraseña
        val derivedKey = deriveKeyFromPassword(password, salt)

        // Descifrar clave privada
        val privateKeyBytes = decryptAES_GCM(encryptedPrivateKey, derivedKey, iv)

        return bytesToPrivateKey(privateKeyBytes)
    }

    /**
     * Obtiene la clave pública almacenada.
     */
    suspend fun getPublicKey(): PublicKey {
        val preferences = this@CryptoManager.context.cryptoDataStore.data.first()
        val publicKeyBytes = preferences[KEY_PUBLIC_KEY]
            ?: throw IllegalStateException("No hay clave pública guardada")
        return bytesToPublicKey(publicKeyBytes)
    }

    /**
     * Obtiene la clave pública en formato Base64.
     */
    suspend fun getPublicKeyBase64(): String {
        val publicKey = getPublicKey()
        return Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
    }

    /**
     * Obtiene el certificado del servidor.
     */
    suspend fun getCertificado(): String? {
        return this@CryptoManager.context.cryptoDataStore.data.map { preferences ->
            preferences[KEY_CERTIFICADO]
        }.first()
    }

    /**
     * Verifica si existen claves almacenadas.
     */
    suspend fun hasStoredKeys(): Boolean {
        val preferences = this@CryptoManager.context.cryptoDataStore.data.first()
        return preferences[KEY_ENCRYPTED_PRIVATE_KEY] != null &&
               preferences[KEY_PUBLIC_KEY] != null
    }

    /**
     * Elimina todas las claves almacenadas (para logout/reset).
     */
    suspend fun clearAllKeys() {
        this@CryptoManager.context.cryptoDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // ==================== UTILIDADES ====================

    /**
     * Convierte ByteArray a String hexadecimal (para debugging).
     */
    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Calcula el hash SHA-256 de datos.
     */
    fun sha256(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance(Constantes.SHA256_ALGORITHM)
        return digest.digest(data)
    }
}

