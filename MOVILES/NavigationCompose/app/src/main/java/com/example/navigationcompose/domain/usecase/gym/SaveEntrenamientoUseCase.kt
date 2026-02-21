package com.example.navigationcompose.domain.usecase.gym

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import com.example.navigationcompose.data.remote.entity.EntrenamientoEntity
import com.example.navigationcompose.domain.model.Entrenamiento
import javax.inject.Inject

class SaveEntrenamientoUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    suspend operator fun invoke(entrenamiento: EntrenamientoEntity): NetworkResult<Entrenamiento> {
        return gymRepository.saveEntrenamiento(entrenamiento)
    }
}

