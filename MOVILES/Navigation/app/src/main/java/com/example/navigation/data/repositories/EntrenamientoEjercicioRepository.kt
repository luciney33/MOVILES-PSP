package com.example.navigation.data.repositories

import com.example.navigation.data.local.dao.EntrenamientoEjercicioDao
import com.example.navigation.data.local.entities.EntrenamientoEjercicioRelacion
import jakarta.inject.Inject

class EntrenamientoEjercicioRepository @Inject constructor(
    private val entrenamientoEjercicioDao: EntrenamientoEjercicioDao
) {
    suspend fun addEjercicioToEntrenamiento(relacion: EntrenamientoEjercicioRelacion) {
        entrenamientoEjercicioDao.addEjercicioToEntrenamiento(relacion)
    }

    suspend fun quitEjercicioDeEntrenamiento(entrenamientoId: Int, ejercicioId: Int) {

        entrenamientoEjercicioDao.quitarEjercicioDeEntrenamiento(entrenamientoId, ejercicioId)
    }

}