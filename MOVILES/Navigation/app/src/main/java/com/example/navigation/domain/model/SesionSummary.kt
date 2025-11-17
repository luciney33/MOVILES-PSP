package com.example.navigation.domain.model

data class SesionSummary(
    val id: Int,
    val entrenamientoNombre: String,
    val fechaInicio: Long,
    val duracionMs: Long,
    val ejerciciosCompletados: Int,
    val volumenTotalKg: Double,
    val ejerciciosTotales: Int = 0
)
