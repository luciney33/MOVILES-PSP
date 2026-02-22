package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import com.example.navigationcompose.domain.model.Secreto
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de secretos accesibles por el usuario actual.
 *
 * Incluye:
 * - Secretos creados por el usuario (es autor)
 * - Secretos compartidos con el usuario
 *
 * NOTA: El contenido viene cifrado desde el servidor.
 * Para ver el contenido descifrado, usar DescifrarSecretoUseCase.
 *
 * @return NetworkResult<List<Secreto>> con contenidoDescifrado = null
 */
class GetSecretosUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Secreto>> {
        return secretosRepository.getSecretos()
    }
}

