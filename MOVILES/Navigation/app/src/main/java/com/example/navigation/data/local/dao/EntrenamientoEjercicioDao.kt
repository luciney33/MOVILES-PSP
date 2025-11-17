package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.navigation.data.local.entity.EntrenamientoEjercicioRelacion

@Dao
interface EntrenamientoEjercicioDao {
    @Insert
    suspend fun addEjercicioToEntrenamiento(relacion: EntrenamientoEjercicioRelacion)

    @Query("DELETE FROM entrenamiento_ejercicio WHERE entrenamientoId = :entrenamientoId AND ejercicioId = :ejercicioId")
    suspend fun quitarEjercicioDeEntrenamiento(entrenamientoId: Int, ejercicioId: Int)

    @Query("UPDATE entrenamiento_ejercicio SET series = :series, repeticiones = :repeticiones WHERE entrenamientoId = :entrenamientoId AND ejercicioId = :ejercicioId")
    suspend fun updateRelation(entrenamientoId: Int, ejercicioId: Int, series: Int, repeticiones: Int): Int

    // Cuenta cuántos ejercicios tiene un entrenamiento (para mostrar en el historial/summary)
    @Query("SELECT COUNT(*) FROM entrenamiento_ejercicio WHERE entrenamientoId = :entrenamientoId")
    suspend fun countByEntrenamientoId(entrenamientoId: Int): Int
}