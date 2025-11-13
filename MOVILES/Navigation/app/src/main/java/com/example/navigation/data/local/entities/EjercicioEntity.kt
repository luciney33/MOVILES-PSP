package com.example.navigation.data.local.entities

import androidx.room.PrimaryKey

data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val grupoMuscular: String,
    val descripcion: String? = null
)
