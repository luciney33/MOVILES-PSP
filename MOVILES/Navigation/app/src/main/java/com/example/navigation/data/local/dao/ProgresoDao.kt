package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.navigation.data.local.entity.ProgresoEntity

@Dao
interface ProgresoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progreso: ProgresoEntity): Long

    @Query("SELECT * FROM progresos ORDER BY fecha DESC")
    suspend fun getAll(): List<ProgresoEntity>

}

