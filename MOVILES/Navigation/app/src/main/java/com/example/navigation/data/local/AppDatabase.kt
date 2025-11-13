package com.example.navigation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.navigation.data.local.dao.EjercicioDao
import com.example.navigation.data.local.dao.EntrenamientoDao
import com.example.navigation.data.local.dao.EntrenamientoEjercicioDao
import com.example.navigation.data.local.entities.EjercicioEntity
import com.example.navigation.data.local.entities.EntrenamientoEjercicioRelacion
import com.example.navigation.data.local.entities.EntrenamientoEntity

@Database(
    entities = [
        EntrenamientoEntity::class,
        EjercicioEntity::class,
        EntrenamientoEjercicioRelacion::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entrenamientoDao(): EntrenamientoDao

    abstract fun entrenamientoEjercicioDao(): EntrenamientoEjercicioDao
    abstract fun ejercicioDao(): EjercicioDao
}