package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progresos")
data class ProgresoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: Long = System.currentTimeMillis(),
    val pesoKg: Double,
    val grasaPercent: Double? = null,
    val notas: String? = null
)

