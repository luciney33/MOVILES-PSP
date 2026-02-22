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

            val hasPublicKeyOnServer = !usuario.publicKey.isNullOrBlank()

            if (!hasPublicKeyOnServer) {
                // This user (e.g., admin) does not have a public key on the server yet.
                // Let's generate one and send it.
                try {
                    val keyPair = cryptoManager.generateRSAKeyPair()
                    val privateKey = keyPair.private
                    val publicKey = keyPair.public

                    val salt = cryptoManager.generateSalt()
                    val derivedKey = cryptoManager.deriveKeyFromPassword(password, salt)

                    val iv = cryptoManager.generateIV()
                    val encryptedPrivateKey = cryptoManager.encryptAES_GCM(privateKey.encoded, derivedKey, iv)

                    // Save all keys locally
                    cryptoManager.saveEncryptedKeys(
                        encryptedPrivateKey = encryptedPrivateKey,
                        salt = salt,
                        ivPrivateKey = iv,
                        publicKey = publicKey.encoded
                    )

                    // Send public key to the server
                    val publicKeyBase64 = Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)

                    when (val updateResult = gymRepository.updatePublicKey(publicKeyBase64)) {
                        is NetworkResult.Success -> {
                            // Server returns the new certificate. We must save this one.
                            certificadoParaGuardar = updateResult.data
                        }
                        is NetworkResult.Error -> {
                            // Error updating public key. The certificate will be missing.
                        }
                    }
                } catch (_: Exception) {
                    // Could not generate or save keys.
                }
            } else {
                // User already has a public key on the server.
                // Let's check if we have it locally.
                val hasStoredKeys = cryptoManager.hasStoredKeys()
                if (!hasStoredKeys) {
                    // Keys are not on the device (e.g., new install).
                    // We can't recover the private key, but we can save the public key
                    // for signature verification purposes.
                    try {
                        val publicKeyBytes = Base64.decode(usuario.publicKey, Base64.NO_WRAP)
                        cryptoManager.savePublicKeyOnly(publicKeyBytes)
                    } catch (_: Exception) {
                        // Ignore error decoding or saving.
                    }
                }
            }

            // Save the definitive certificate (either the original one or the new one)
            if (!certificadoParaGuardar.isNullOrBlank()) {
                try {
                    cryptoManager.saveCertificado(certificadoParaGuardar)
                } catch (_: Exception) {
                    // Ignore error saving certificate.
                }
            }

            // Save password in session for this run
            sessionManager.savePasswordInSession(password)
        }

        return result
    }
}