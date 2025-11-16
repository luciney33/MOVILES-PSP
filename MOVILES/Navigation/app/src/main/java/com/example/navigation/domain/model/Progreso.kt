package com.example.navigation.domain.model



data class Progreso(
    val ejercicioId: Int,
    val nombre: String,
    val grupoMuscular: String,
    val ultimoPeso: String,
    val ultimoDetalle: String,
    val record: String,
    val tendenciaUp: Boolean
)

