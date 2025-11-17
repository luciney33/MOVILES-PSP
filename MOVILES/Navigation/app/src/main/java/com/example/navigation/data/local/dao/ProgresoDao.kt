package com.example.navigation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.navigation.data.local.entity.ProgresoEntity

@Dao
interface ProgresoDao {

    @Insert
    suspend fun insert(progreso: ProgresoEntity): Long

}

