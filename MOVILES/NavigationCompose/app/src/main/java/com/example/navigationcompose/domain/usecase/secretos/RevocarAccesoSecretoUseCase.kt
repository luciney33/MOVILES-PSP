package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import javax.inject.Inject

/**
 * Caso de uso para revocar el acceso de un usuario a un secreto.
 *
 * Funcionalidad:
 * - Elimina la clave AES cifrada correspondiente a ese usuario del servidor
 * - El usuario ya no puede descifrar el secreto
 * - Solo el autor puede revocar accesos
 *
 * Seguridad:
 * - El servidor verifica que quien revoca sea el autor
 * - No se puede revocar el propio acceso si eres el autor
 *
 * @param secretoId ID del secreto
 * @param usuarioId ID del usuario al que se revoca acceso
 */
class RevocarAccesoSecretoUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(
        secretoId: Long,
        usuarioId: Long
    ): NetworkResult<Unit> {
        return secretosRepository.revocarAcceso(
            secretoId = secretoId,
            usuarioId = usuarioId
        )
    }
}

