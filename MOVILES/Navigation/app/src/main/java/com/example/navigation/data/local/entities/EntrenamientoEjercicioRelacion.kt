package com.example.navigation.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

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
    ]
)
data class EntrenamientoEjercicioRelacion(
    val entrenamientoId: Int,
    val ejercicioId: Int,
    val orden: Int? = null,
    val series: Int? = null,
    val repeticiones: Int? = null
)
