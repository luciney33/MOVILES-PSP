package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar permanentemente un secreto.
 *
 * Funcionalidad:
 * - Elimina el secreto del servidor
 * - Elimina todas las claves AES cifradas asociadas
 * - El contenido cifrado se pierde permanentemente
 * - Solo el autor puede eliminar
 *
 * Seguridad:
 * - El servidor verifica que quien elimina sea el autor
 * - La operación es irreversible
 *
 * IMPORTANTE: El contenido no se puede recuperar después de eliminar.
 *
 * @param secretoId ID del secreto a eliminar
 */
class EliminarSecretoUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(secretoId: Long): NetworkResult<Unit> {
        return secretosRepository.eliminarSecreto(secretoId)
    }
}

