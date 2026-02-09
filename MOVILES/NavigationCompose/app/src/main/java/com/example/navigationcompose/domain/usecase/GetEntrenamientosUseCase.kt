package com.example.navigationcompose.domain.usecase

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import com.example.navigationcompose.domain.model.Entrenamiento
import javax.inject.Inject

class GetEntrenamientosUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Entrenamiento>> {
        return gymRepository.getEntrenamientos()
    }
}

