package com.example.navigationcompose.domain.usecase.gym

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import com.example.navigationcompose.domain.model.Entrenamiento
import javax.inject.Inject

class GetEntrenamientoByIdUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    suspend operator fun invoke(id: Long): NetworkResult<Entrenamiento> {
        return gymRepository.getEntrenamientoById(id)
    }
}

