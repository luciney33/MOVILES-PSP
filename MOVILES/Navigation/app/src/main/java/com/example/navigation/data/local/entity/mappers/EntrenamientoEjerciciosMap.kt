package com.example.navigation.data.local.entity.mappers

import com.example.navigation.data.local.entity.EjercicioEntity
import com.example.navigation.data.local.entity.EntrenamientoConEjerciciosEntity
import com.example.navigation.data.local.entity.EntrenamientoEntity
import com.example.navigation.domain.model.Ejercicio
import com.example.navigation.domain.model.Entrenamiento

fun EntrenamientoEntity.toEntrenamiento(): Entrenamiento =
    Entrenamiento(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion,
        duracionMin = this.duracionMin
    )


fun EjercicioEntity.toEjercicio(): Ejercicio =
    Ejercicio(
        id = this.id,
        nombre = this.nombre,
        grupoMuscular = this.grupoMuscular,
        descripcion = this.descripcion,
        iconoResName = this.iconoResName
    )


fun EntrenamientoConEjerciciosEntity.toDomain(): Entrenamiento =
    Entrenamiento(
        id = this.entrenamiento.id,
        nombre = this.entrenamiento.nombre,
        descripcion = this.entrenamiento.descripcion,
        duracionMin = this.entrenamiento.duracionMin,
        ejercicios = this.ejercicios.map { it.toEjercicio() }
    )
