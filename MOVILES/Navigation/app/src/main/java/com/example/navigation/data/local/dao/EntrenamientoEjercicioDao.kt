package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.navigation.data.local.entities.EntrenamientoEjercicioRelacion

@Dao
interface EntrenamientoEjercicioDao {
    @Insert
    suspend fun addEjercicioToEntrenamiento(relacion: EntrenamientoEjercicioRelacion)

    @Query("DELETE FROM entrenamiento_ejercicio WHERE entrenamientoId = :entrenamientoId AND ejercicioId = :ejercicioId")
    suspend fun quitarEjercicioDeEntrenamiento(entrenamientoId: Int, ejercicioId: Int)
}