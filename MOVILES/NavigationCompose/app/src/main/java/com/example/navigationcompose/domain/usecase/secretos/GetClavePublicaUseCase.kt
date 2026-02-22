package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.security.CryptoManager
import javax.inject.Inject

/**
 * Caso de uso para obtener la clave pública del usuario actual.
 *
 * Utilidad:
 * - Mostrar la clave pública en el perfil del usuario
 * - Verificar que coincida con la del servidor
 * - Debugging de problemas de cifrado
 *
 * @return NetworkResult<String> con la clave pública en Base64
 */
class GetClavePublicaUseCase @Inject constructor(
    private val cryptoManager: CryptoManager
) {
    suspend operator fun invoke(): NetworkResult<String> {
        return try {
            val clavePublicaBase64 = cryptoManager.getPublicKeyBase64()
            NetworkResult.Success(clavePublicaBase64)
        } catch (e: Exception) {
            NetworkResult.Error("Error al obtener clave pública: ${e.message}")
        }
    }
}

