package com.example.navigationcompose.domain.usecase

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.GymRepository
import com.example.navigationcompose.data.remote.entity.LoginRequest
import com.example.navigationcompose.data.remote.entity.LoginResponse
import com.example.navigationcompose.data.security.CryptoManager
import javax.inject.Inject

/**
 * Caso de uso para el login de usuario.
 *
 * Responsabilidades:
 * 1. Autenticar al usuario en el servidor
 * 2. Guardar tokens de sesión
 * 3. Guardar certificado del servidor (si el usuario tiene clave pública)
 *
 * IMPORTANTE: El certificado se guarda para poder verificar claves públicas
 * de otros usuarios cuando se compartan secretos.
 */
class LoginUseCase @Inject constructor(
    private val gymRepository: GymRepository,
    private val cryptoManager: CryptoManager
) {
    suspend operator fun invoke(username: String, password: String): NetworkResult<LoginResponse> {
        val result = gymRepository.login(LoginRequest(username, password))

        // Si el login fue exitoso y el usuario tiene certificado, guardarlo
        if (result is NetworkResult.Success) {
            result.data.usuario.certificado?.let { certificadoBase64 ->
                try {
                    // Guardar certificado en DataStore para verificación de otros usuarios
                    cryptoManager.saveCertificado(certificadoBase64)
                } catch (_: Exception) {
                    // Log del error pero no fallar el login
                    // El certificado se puede obtener después si es necesario
                }
            }
        }

        return result
    }
}
