package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import javax.inject.Inject

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

