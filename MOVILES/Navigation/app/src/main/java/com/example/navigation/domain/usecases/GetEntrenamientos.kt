package com.example.navigation.domain.usecases

import com.example.navigation.data.repository.EntrenamientoRepository
import com.example.navigation.domain.model.Entrenamiento
import jakarta.inject.Inject

class GetEntrenamientos
@Inject constructor( private val repository: EntrenamientoRepository)
{
    operator suspend fun  invoke(): List<Entrenamiento>{
        return repository.getEntrenamientos()
    }
}