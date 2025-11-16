package com.example.navigation.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
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
    val id: Int ,
    val sesionId: Int,
    val ejercicioId: Int,
    val orden: Int,
    val volumenKg: Double,
    val notas: String
)
