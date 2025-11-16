package com.example.navigation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "entrenamientos"
)
data class EntrenamientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val duracionMin: Int,
)
