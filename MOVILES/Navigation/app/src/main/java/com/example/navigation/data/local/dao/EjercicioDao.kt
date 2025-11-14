package com.example.navigation.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.navigation.data.local.entities.EjercicioEntity
import com.example.navigation.domain.model.GrupoMuscularEstadistica

@Dao
interface EjercicioDao {
    @Insert
    suspend fun insert(ejercicio: EjercicioEntity): Long

    @Update
    suspend fun update(ejercicio: EjercicioEntity)

    @Delete
    suspend fun delete(ejercicio: EjercicioEntity)

    @Query("SELECT * FROM ejercicios ORDER BY nombre ASC")
    fun getAllEjerciciosLive(): List<EjercicioEntity>

    @Query("SELECT * FROM ejercicios ORDER BY nombre ASC")
    suspend fun getAllEjercicios(): List<EjercicioEntity>

    @Query("SELECT * FROM EJERCICIOS WHERE id = :id")
    fun getEjercicioById(id: Int): EjercicioEntity


    @Query("SELECT * FROM ejercicios WHERE grupoMuscular = :grupo ORDER BY nombre ASC")
    fun getByGrupoMuscularLive(grupo: String): List<EjercicioEntity>

    @Query("SELECT COUNT(*) FROM ejercicios")
    fun getTotalEjercicios(): Int

    @Query("SELECT grupoMuscular, COUNT(*) as cantidad FROM ejercicios GROUP BY grupoMuscular")
    fun getEjerciciosPorGrupo(): List<GrupoMuscularEstadistica>

}