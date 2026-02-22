package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import com.example.navigationcompose.domain.model.Usuario
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de usuarios con los que se pueden compartir secretos.
 *
 * Funcionalidades:
 * - Obtiene usuarios del servidor
 * - Verifica certificados de cada usuario
 * - Marca como verificados solo si la firma del servidor es válida
 *
 * @return NetworkResult<List<Usuario>> con campo certificadoVerificado
 */
class GetUsuariosParaCompartirUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Usuario>> {
        return secretosRepository.getUsuarios()
    }
}

