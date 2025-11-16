package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sesion_ejercicios",
    foreignKeys = [
        ForeignKey(
            entity = SesionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sesionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EjercicioEntity::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
)
data class SesionEjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sesionId: Int,
    val ejercicioId: Int,
    val orden: Int = 0,
    val volumenKg: Double = 0.0,
    val notas: String? = null
)

