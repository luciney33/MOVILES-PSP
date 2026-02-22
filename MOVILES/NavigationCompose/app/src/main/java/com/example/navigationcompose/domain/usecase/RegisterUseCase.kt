package com.example.navigationcompose.domain.usecase

import android.util.Base64
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.GymRepository
import com.example.navigationcompose.data.remote.entity.UsuarioEntity
import com.example.navigationcompose.data.security.CryptoManager
import com.example.navigationcompose.domain.model.Usuario
import javax.inject.Inject

/**
 * Caso de uso para el registro de usuario CON generación de claves RSA.
 *
 * Flujo completo de registro seguro:
 * 1. Valida la contraseña (mínimo 8 caracteres)
 * 2. Genera par de claves RSA-4096
 * 3. Cifra la clave privada con PBKDF2 + AES-GCM usando la contraseña
 * 4. Guarda las claves cifradas en DataStore
 * 5. Incluye la clave pública en el registro al servidor
 * 6. El servidor firma la clave pública (certificado)
 * 7. Guarda el certificado firmado por el servidor
 *
 * IMPORTANTE: La clave privada NUNCA sale del dispositivo.
 */
class RegisterUseCase @Inject constructor(
    private val gymRepository: GymRepository,
    private val cryptoManager: CryptoManager
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        nombre: String,
        password: String
    ): NetworkResult<Usuario> {
        return try {
            // 1. Validar contraseña
            if (password.length < 8) {
                return NetworkResult.Error(Constantes.ERROR_PASSWORD_MINIMA)
            }

            // 2. Generar par de claves RSA-4096
            val keyPair = cryptoManager.generateRSAKeyPair()
            val publicKey = keyPair.public
            val privateKey = keyPair.private

            // 3. Generar salt y derivar clave de cifrado desde la contraseña
            val salt = cryptoManager.generateSalt()
            val derivedKey = cryptoManager.deriveKeyFromPassword(password, salt)

            // 4. Generar IV y cifrar clave privada
            val iv = cryptoManager.generateIV()
            val privateKeyBytes = privateKey.encoded
            val encryptedPrivateKey = cryptoManager.encryptAES_GCM(privateKeyBytes, derivedKey, iv)

            // 5. Guardar claves cifradas en DataStore
            val publicKeyBytes = publicKey.encoded
            cryptoManager.saveEncryptedKeys(
                encryptedPrivateKey = encryptedPrivateKey,
                salt = salt,
                ivPrivateKey = iv,
                publicKey = publicKeyBytes
            )

            // 6. Convertir clave pública a Base64 para enviar al servidor
            val publicKeyBase64 = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)

            // 7. Crear UsuarioEntity con la clave pública
            val usuarioEntity = UsuarioEntity(
                id = 0,
                username = username,
                email = email,
                nombre = nombre,
                password = password, // El servidor la hasheará
                publicKey = publicKeyBase64,
                certificado = null // El servidor lo generará
            )

            // 8. Registrar usuario en el servidor
            val result = gymRepository.register(usuarioEntity)

            // 9. Si el registro fue exitoso y el servidor devolvió un certificado, guardarlo
            when (result) {
                is NetworkResult.Success -> {
                    val usuario = result.data
                    // Si el servidor devuelve certificado, guardarlo
                    usuario.certificado?.let { certificadoBytes ->
                        val certificadoBase64 = Base64.encodeToString(certificadoBytes, Base64.NO_WRAP)
                        cryptoManager.saveCertificado(certificadoBase64)
                    }
                    NetworkResult.Success(usuario)
                }
                is NetworkResult.Error -> {
                    // Si el registro falló, limpiar las claves guardadas
                    cryptoManager.clearAllKeys()
                    result
                }
            }
        } catch (e: Exception) {
            // En caso de error, limpiar claves guardadas
            try {
                cryptoManager.clearAllKeys()
            } catch (_: Exception) {
                // Ignorar error al limpiar
            }
            NetworkResult.Error("${Constantes.ERROR_GENERAR_CLAVES}${e.message}")
        }
    }
}
