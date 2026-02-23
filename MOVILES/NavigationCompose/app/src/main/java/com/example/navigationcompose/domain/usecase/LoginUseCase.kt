package com.example.navigationcompose.domain.usecase

import android.util.Base64
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.GymRepository
import com.example.navigationcompose.data.remote.entity.LoginRequest
import com.example.navigationcompose.data.remote.entity.LoginResponse
import com.example.navigationcompose.data.security.CryptoManager
import com.example.navigationcompose.data.security.SessionManager
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val gymRepository: GymRepository,
    private val cryptoManager: CryptoManager,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(username: String, password: String): NetworkResult<LoginResponse> {
        val result = gymRepository.login(LoginRequest(username, password))

        if (result is NetworkResult.Success) {
            val usuario = result.data.usuario
            var certificadoParaGuardar = usuario.certificado // Get initial certificate

            sessionManager.setCurrentUser(usuario)

            val hasPublicKeyOnServer = !usuario.publicKey.isNullOrBlank()

            if (!hasPublicKeyOnServer) {
                try {
                    val keyPair = cryptoManager.generateRSAKeyPair()
                    val privateKey = keyPair.private
                    val publicKey = keyPair.public

                    val salt = cryptoManager.generateSalt()
                    val derivedKey = cryptoManager.deriveKeyFromPassword(password, salt)

                    val iv = cryptoManager.generateIV()
                    val encryptedPrivateKey = cryptoManager.encryptAES_GCM(privateKey.encoded, derivedKey, iv)
                    cryptoManager.saveEncryptedKeys(
                        username = username,
                        encryptedPrivateKey = encryptedPrivateKey,
                        salt = salt,
                        ivPrivateKey = iv,
                        publicKey = publicKey.encoded
                    )

                    val publicKeyBase64 = Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)

                    when (val updateResult = gymRepository.updatePublicKey(publicKeyBase64)) {
                        is NetworkResult.Success -> {
                            certificadoParaGuardar = updateResult.data
                        }
                        is NetworkResult.Error -> {
                        }
                    }
                } catch (_: Exception) {
                }
            } else {
                val hasStoredKeys = cryptoManager.hasStoredKeys(username)
                if (!hasStoredKeys) {
                    try {
                        val publicKeyBytes = Base64.decode(usuario.publicKey, Base64.NO_WRAP)
                        cryptoManager.savePublicKeyOnly(publicKeyBytes)
                    } catch (_: Exception) {
                    }
                }
            }

            if (!certificadoParaGuardar.isNullOrBlank()) {
                try {
                    cryptoManager.saveCertificado(certificadoParaGuardar)
                } catch (_: Exception) {
                }
            }
            sessionManager.savePasswordInSession(password)
        }

        return result
    }
}