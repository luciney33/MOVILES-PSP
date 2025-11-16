package com.example.navigation.data.repository

import com.example.navigation.data.local.dao.EntrenamientoEjercicioDao
import com.example.navigation.data.local.entity.EntrenamientoEjercicioRelacion
import javax.inject.Inject

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