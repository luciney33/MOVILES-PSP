package com.example.navigation.data.repository

import com.example.navigation.data.local.dao.EjercicioDao
import com.example.navigation.data.local.entity.mappers.toEjercicio
import com.example.navigation.data.local.entity.mappers.toEjercicioEntity
import com.example.navigation.domain.model.Ejercicio
import javax.inject.Inject

class EjercicioRepository@Inject constructor(
    private val ejercicioDao : EjercicioDao

) {
    suspend fun getAllEjercicios() : List<Ejercicio>{
        return ejercicioDao.getAllEjercicios().map { it.toEjercicio() }
    }

    suspend fun insertEjercicio(ejercicio: Ejercicio){
        ejercicioDao.insert(
            ejercicio.toEjercicioEntity()
        )
    }

    suspend fun deleteEjercicio(ejercicio: Ejercicio){
        ejercicioDao.delete(
            ejercicio.toEjercicioEntity()
        )
    }

}