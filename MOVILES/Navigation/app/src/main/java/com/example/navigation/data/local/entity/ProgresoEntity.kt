package com.example.navigation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progresos")
data class ProgresoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int ,
    val fecha: Long = System.currentTimeMillis(),
    val pesoKg: Double,
    val grasaPercent: Double,
    val notas: String
)

