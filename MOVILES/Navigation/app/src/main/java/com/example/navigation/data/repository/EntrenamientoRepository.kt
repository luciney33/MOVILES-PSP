package com.example.navigation.data.repository


import com.example.navigation.data.local.dao.EntrenamientoDao
import com.example.navigation.data.local.entity.mappers.*
import com.example.navigation.domain.model.Entrenamiento
import javax.inject.Inject

class EntrenamientoRepository @Inject constructor(
    private val entrenamientoDao : EntrenamientoDao
){
    suspend fun getEntrenamientos(): List<Entrenamiento> {
        val entrenamientos = entrenamientoDao.getAll()
        return entrenamientos.map { it.toEntrenamiento() }
    }
    suspend fun getAllConEjercicios(): List<Entrenamiento>{
        return entrenamientoDao.getAllConEjercicios().map { it.toDomain() }
    }
}