package com.example.navigationcompose.domain.usecase

import android.util.Base64
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.GymRepository
import com.example.navigationcompose.data.remote.entity.UsuarioEntity
import com.example.navigationcompose.data.security.CryptoManager
import com.example.navigationcompose.data.security.SessionManager
import com.example.navigationcompose.domain.model.Usuario
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val gymRepository: GymRepository,
    private val cryptoManager: CryptoManager,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        nombre: String,
        password: String
    ): NetworkResult<Usuario> {
        return try {
            if (password.length < 8) {
                return NetworkResult.Error(Constantes.ERROR_PASSWORD_MINIMA)
            }

            val keyPair = cryptoManager.generateRSAKeyPair()
            val publicKey = keyPair.public
            val privateKey = keyPair.private

            val salt = cryptoManager.generateSalt()
            val derivedKey = cryptoManager.deriveKeyFromPassword(password, salt)

            val iv = cryptoManager.generateIV()
            val privateKeyBytes = privateKey.encoded
            val encryptedPrivateKey = cryptoManager.encryptAES_GCM(privateKeyBytes, derivedKey, iv)

            val publicKeyBytes = publicKey.encoded
            cryptoManager.saveEncryptedKeys(
                encryptedPrivateKey = encryptedPrivateKey,
                salt = salt,
                ivPrivateKey = iv,
                publicKey = publicKeyBytes
            )

            val publicKeyBase64 = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)

            val usuarioEntity = UsuarioEntity(
                id = 0,
                username = username,
                email = email,
                nombre = nombre,
                password = password,
                publicKey = publicKeyBase64,
                certificado = null
            )

            val result = gymRepository.register(usuarioEntity)

            when (result) {
                is NetworkResult.Success -> {
                    val usuario = result.data
                    usuario.certificado?.let { certificadoBytes ->
                        val certificadoBase64 = Base64.encodeToString(certificadoBytes, Base64.NO_WRAP)
                        cryptoManager.saveCertificado(certificadoBase64)
                    }
                    // Guardar contraseña en sesión (memoria RAM, NO se persiste)
                    sessionManager.savePasswordInSession(password)
                    NetworkResult.Success(usuario)
                }
                is NetworkResult.Error -> {
                    cryptoManager.clearAllKeys()
                    result
                }
            }
        } catch (e: Exception) {
            try {
                cryptoManager.clearAllKeys()
            } catch (_: Exception) {
            }
            NetworkResult.Error("${Constantes.ERROR_GENERAR_CLAVES}${e.message}")
        }
    }
}
