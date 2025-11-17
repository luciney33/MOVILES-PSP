package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.navigation.data.local.entity.SesionEntity

@Dao
interface SesionDao {

    @Insert
    suspend fun insert(sesion: SesionEntity): Long

    @Query("SELECT * FROM sesiones WHERE id = :id")
    suspend fun getById(id: Int): SesionEntity?

    @Query("SELECT * FROM sesiones ORDER BY fechaInicio DESC")
    suspend fun getAll(): List<SesionEntity>

    @Query("UPDATE sesiones SET duracionMs = :duracion WHERE id = :id")
    suspend fun updateDuracion(id: Int, duracion: Long): Int

}