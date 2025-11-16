package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "entrenamientos"
)
data class EntrenamientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String? = null,
    val duracionMin: Int? = null,
)
