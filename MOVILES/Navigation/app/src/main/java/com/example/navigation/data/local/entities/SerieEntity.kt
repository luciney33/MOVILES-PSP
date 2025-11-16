package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "series",
    foreignKeys = [
        ForeignKey(
            entity = SesionEjercicioEntity::class,
            parentColumns = ["id"],
            childColumns = ["sesionEjercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class SerieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sesionEjercicioId: Int,
    val numero: Int,
    val pesoKg: Double,
    val repeticiones: Int,
    val completada: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

