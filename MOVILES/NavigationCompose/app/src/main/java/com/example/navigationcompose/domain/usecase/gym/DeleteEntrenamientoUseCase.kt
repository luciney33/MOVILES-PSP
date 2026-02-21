package com.example.navigationcompose.domain.usecase.gym

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import javax.inject.Inject

class DeleteEntrenamientoUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> {
        return gymRepository.deleteEntrenamiento(id)
    }
}

