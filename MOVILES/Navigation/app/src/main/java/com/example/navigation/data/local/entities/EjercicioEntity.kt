package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "ejercicios"
)
data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val grupoMuscular: String? = null,
    val descripcion: String? = null,
    val iconoResName: String? = null
)
