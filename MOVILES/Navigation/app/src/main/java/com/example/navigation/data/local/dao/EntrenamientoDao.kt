package com.example.navigation.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.navigation.data.local.entities.EntrenamientoConEjercicios
import com.example.navigation.data.local.entities.EntrenamientoEntity
import com.example.navigation.domain.model.Entrenamiento

@Dao
interface EntrenamientoDao {

    @Insert
    suspend fun insert(entrenamiento: EntrenamientoEntity): Long

    @Update
    suspend fun update(entrenamiento: EntrenamientoEntity)

    @Delete
    suspend fun delete(entrenamiento: EntrenamientoEntity)
    @Transaction
    @Query("SELECT * FROM entrenamientos WHERE id = :id")
    fun getEntrenamientoConEjercicios(id: Int):  EntrenamientoConEjercicios

    @Query("SELECT * FROM entrenamientos")
    fun getAllLive() : List<EntrenamientoEntity>

    @Query("SELECT * FROM entrenamientos")
    suspend fun getAll() : List<EntrenamientoEntity>

    @Query("SELECT * FROM entrenamientos WHERE id = :id")
    fun getEntrenamientoById(id: Int): EntrenamientoEntity

    @Transaction
    @Query("SELECT * FROM entrenamientos ORDER BY ultimaActualizacion DESC")
    fun getAllConEjercicios(): List<EntrenamientoConEjercicios>

    @Query("SELECT COUNT(*) FROM entrenamientos")
    fun getTotalEntrenamientos(): Int

    @Query("SELECT * FROM entrenamientos ORDER BY ultimaActualizacion DESC LIMIT 5")
    fun getEntrenamientosRecientes(): List<EntrenamientoEntity>
}