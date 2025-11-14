package com.example.navigation.data.repositories


import com.example.navigation.data.local.dao.EntrenamientoDao
import com.example.navigation.data.local.entities.mappers.toDomain
import com.example.navigation.data.local.entities.mappers.toEntrenamiento
import com.example.navigation.domain.model.Entrenamiento
import com.example.navigation.domain.model.EntrenamientoConEjercicios
import jakarta.inject.Inject

class EntrenamientoRepository @Inject constructor(
    private val entrenamientoDao : EntrenamientoDao
){
    suspend fun getEntrenamientos(): List<Entrenamiento> {
        return entrenamientoDao.getAll().map { it.toEntrenamiento() }
    }
     fun getAllConEjercicios(): List<EntrenamientoConEjercicios>{
        return entrenamientoDao.getAllConEjercicios().map { it.toDomain() }
    }
}