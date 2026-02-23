package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import com.example.navigationcompose.domain.model.SecretoDescifrado
import javax.inject.Inject

class DescifrarSecretoUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(
        secretoId: Long,
        passwordUsuario: String,
        autorId: Long? = null
    ): NetworkResult<SecretoDescifrado> {
        return secretosRepository.descifrarSecreto(
            secretoId = secretoId,
            passwordUsuario = passwordUsuario,
            autorId = autorId
        )
    }
}
