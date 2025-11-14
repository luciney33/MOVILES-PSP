package com.example.navigation.data.local.entities.mappers

import com.example.navigation.data.local.entities.EjercicioEntity
import com.example.navigation.data.local.entities.EntrenamientoConEjerciciosEntity
import com.example.navigation.data.local.entities.EntrenamientoEntity
import com.example.navigation.domain.model.Ejercicio
import com.example.navigation.domain.model.Entrenamiento
import com.example.navigation.domain.model.EntrenamientoConEjercicios

    fun EntrenamientoEntity.toEntrenamiento(): Entrenamiento =
        Entrenamiento(
            id = this.id,
            nombre = this.nombre,
            descripcion = this.descripcion
        )

    fun Entrenamiento.toEntrenamientoEntity(): EntrenamientoEntity =
        EntrenamientoEntity(
            id = this.id,
            nombre = this.nombre,
            descripcion = this.descripcion
        )

    fun EjercicioEntity.toEjercicio(): Ejercicio =
        Ejercicio(
            id = this.id,
            nombre = this.nombre,
            grupoMuscular = this.grupoMuscular
        )

    fun Ejercicio.toEjercicioEntity(): EjercicioEntity =
        EjercicioEntity(
            id = this.id,
            nombre = this.nombre,
            grupoMuscular = this.grupoMuscular
        )

    fun EntrenamientoConEjerciciosEntity.toDomain(): EntrenamientoConEjercicios =
        EntrenamientoConEjercicios(
            entrenamiento = this.entrenamiento.toEntrenamiento(),
            ejercicios = this.ejercicios.map { it.toEjercicio() }
        )

    fun EntrenamientoConEjercicios.toEntity(): EntrenamientoConEjerciciosEntity =
        EntrenamientoConEjerciciosEntity(
            entrenamiento = this.entrenamiento.toEntrenamientoEntity(),
            ejercicios = this.ejercicios.map { it.toEjercicioEntity() }
        )
