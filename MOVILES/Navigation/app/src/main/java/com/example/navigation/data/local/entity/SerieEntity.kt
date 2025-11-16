package com.example.navigation.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
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
    val id: Int ,
    val sesionEjercicioId: Int,
    val numero: Int,
    val pesoKg: Double,
    val repeticiones: Int,
    val completada: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

