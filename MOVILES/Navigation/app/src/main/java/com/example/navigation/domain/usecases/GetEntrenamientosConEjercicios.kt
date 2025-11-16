package com.example.navigation.domain.usecases

import com.example.navigation.data.repository.EntrenamientoRepository
import jakarta.inject.Inject

class GetEntrenamientosConEjercicios
@Inject constructor( private val repository: EntrenamientoRepository) {
    suspend operator fun invoke() =
        repository.getAllConEjercicios()
}