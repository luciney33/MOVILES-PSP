package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.navigation.data.local.entity.EntrenamientoConEjerciciosEntity
import com.example.navigation.data.local.entity.EntrenamientoEntity

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
    suspend fun getEntrenamientoConEjercicios(id: Int):  EntrenamientoConEjerciciosEntity

    @Query("SELECT * FROM entrenamientos")
    suspend fun getAll() : List<EntrenamientoEntity>

    @Query("SELECT * FROM entrenamientos WHERE id = :id")
    suspend fun getEntrenamientoById(id: Int): EntrenamientoEntity

    @Transaction
    @Query("SELECT * FROM entrenamientos")
    suspend fun getAllConEjercicios(): List<EntrenamientoConEjerciciosEntity>

    @Query("SELECT COUNT(*) FROM entrenamientos order by duracionMin")
    fun getTotalEntrenamientos(): Int

    @Query("SELECT * FROM entrenamientos order by duracionMin DESC LIMIT 5")
    fun getEntrenamientosRecientes(): List<EntrenamientoEntity>
}