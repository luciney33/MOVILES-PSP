package com.example.navigation.data.local

import androidx.room.Database
import com.example.navigation.data.local.entities.EjercicioEntity
import com.example.navigation.data.local.entities.EntrenamientoEntity

@Database(
    entities = [EntrenamientoEntity::class],[EjercicioEntity::class],
    version = 1,
    exportSchema = false
)
class AppDatabase {
}