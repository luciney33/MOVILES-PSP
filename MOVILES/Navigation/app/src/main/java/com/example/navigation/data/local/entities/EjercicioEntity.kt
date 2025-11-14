package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.navigation.domain.model.Ejercicio

@Entity(
    tableName = "ejercicios"
)
data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val grupoMuscular: String,
)

