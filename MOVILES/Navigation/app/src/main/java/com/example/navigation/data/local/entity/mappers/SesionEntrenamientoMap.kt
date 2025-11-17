package com.example.navigation.data.local.entity.mappers

import com.example.navigation.data.local.entity.SesionEntity
import com.example.navigation.data.local.entity.SesionEjercicioConName
import com.example.navigation.data.local.entity.SesionEjercicioEntity
import com.example.navigation.domain.model.Sesion
import com.example.navigation.domain.model.SesionEjercicio
import com.example.navigation.domain.model.SesionSummary

fun toDomainSesion(entity: SesionEntity?): Sesion? {
    return entity?.let {
        Sesion(
            id = it.id,
            entrenamientoId = it.entrenamientoId,
            fechaInicio = it.fechaInicio,
            duracionMs = it.duracionMs,
            ejerciciosCompletados = it.ejerciciosCompletados,
            notas = it.notas
        )
    }
}

fun toDomainSesionEjercicio(entity: SesionEjercicioConName): SesionEjercicio {
    return SesionEjercicio(
        id = entity.id,
        sesionId = entity.sesionId,
        ejercicioId = entity.ejercicioId,
        nombreEjercicio = entity.nombreEjercicio,
        orden = entity.orden,
        volumenKg = entity.volumenKg,
        notas = entity.notas,
        series = entity.series,
        repeticiones = entity.repeticiones
    )
}



fun toEntitySesionEjercicio(domain: SesionEjercicio): SesionEjercicioEntity {
    return SesionEjercicioEntity(
        id = domain.id,
        sesionId = domain.sesionId,
        ejercicioId = domain.ejercicioId,
        orden = domain.orden,
        volumenKg = domain.volumenKg,
        notas = domain.notas
    )
}

fun toDomainSesionSummary(
    entity: SesionEntity,
    entrenamientoNombre: String,
    volumenTotalKg: Double,
    ejerciciosTotales: Int
): SesionSummary {
    return SesionSummary(
        id = entity.id,
        entrenamientoNombre = entrenamientoNombre,
        fechaInicio = entity.fechaInicio,
        duracionMs = entity.duracionMs,
        ejerciciosCompletados = entity.ejerciciosCompletados,
        volumenTotalKg = volumenTotalKg,
        ejerciciosTotales = ejerciciosTotales
    )
}
