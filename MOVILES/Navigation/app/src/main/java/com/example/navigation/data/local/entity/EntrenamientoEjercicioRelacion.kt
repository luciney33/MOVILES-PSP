package com.example.navigation.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "entrenamiento_ejercicio",
    primaryKeys = ["entrenamientoId", "ejercicioId"],
    foreignKeys = [
        ForeignKey(
            entity = EntrenamientoEntity::class,
            parentColumns = ["id"],
            childColumns = ["entrenamientoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EjercicioEntity::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class EntrenamientoEjercicioRelacion(
    val entrenamientoId: Int,
    val ejercicioId: Int,
    val orden: Int,
    val series: Int,
    val repeticiones: Int,
    val descansoSeg: Int
)
