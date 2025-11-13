package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.navigation.data.local.entities.EjercicioEntity

@Dao
interface EjercicioDao {
    @Query("SELECT * FROM entrenamientos ORDER BY nombre ASC")
    suspend fun getAllEjercicios(): List<EjercicioEntity>

    @Query("SELECT * FROM entrenamientos WHERE id = :id")
    suspend fun getEjercicio(id: Long): EjercicioEntity


}