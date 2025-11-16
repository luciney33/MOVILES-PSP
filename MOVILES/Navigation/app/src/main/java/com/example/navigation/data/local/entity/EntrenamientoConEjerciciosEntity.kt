package com.example.navigation.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

//join pa saber cuantos ejercicios tiene un entrenamiento
data class EntrenamientoConEjerciciosEntity(
    @Embedded val entrenamiento : EntrenamientoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EntrenamientoEjercicioRelacion::class,
            parentColumn = "entrenamientoId",
            entityColumn = "ejercicioId"
        )
    )
    val ejercicios: List<EjercicioEntity> = emptyList()
)
