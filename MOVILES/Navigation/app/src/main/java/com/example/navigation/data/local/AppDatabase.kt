package com.example.navigation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.navigation.data.local.dao.EjercicioDao
import com.example.navigation.data.local.dao.EntrenamientoDao
import com.example.navigation.data.local.dao.EntrenamientoEjercicioDao
import com.example.navigation.data.local.dao.SesionDao
import com.example.navigation.data.local.dao.SesionEjercicioDao
import com.example.navigation.data.local.dao.ProgresoDao
import com.example.navigation.data.local.entity.EjercicioEntity
import com.example.navigation.data.local.entity.EntrenamientoEjercicioRelacion
import com.example.navigation.data.local.entity.EntrenamientoEntity
import com.example.navigation.data.local.entity.ProgresoEntity
import com.example.navigation.data.local.entity.SerieEntity
import com.example.navigation.data.local.entity.SesionEjercicioEntity
import com.example.navigation.data.local.entity.SesionEntity

@Database(
    entities = [
        EntrenamientoEntity::class,
        EjercicioEntity::class,
        EntrenamientoEjercicioRelacion::class,
        SesionEntity::class,
        SesionEjercicioEntity::class,
        SerieEntity::class,
        ProgresoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entrenamientoDao(): EntrenamientoDao
    abstract fun entrenamientoEjercicioDao(): EntrenamientoEjercicioDao
    abstract fun ejercicioDao(): EjercicioDao

    abstract fun sesionDao(): SesionDao
    abstract fun sesionEjercicioDao(): SesionEjercicioDao
    abstract fun progresoDao(): ProgresoDao


}