package com.example.navigationcompose.domain.usecase.gym

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.GymRepository
import com.example.navigationcompose.domain.model.Ejercicio
import javax.inject.Inject

class GetEjerciciosUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Ejercicio>> {
        return gymRepository.getEjercicios()
    }
}

