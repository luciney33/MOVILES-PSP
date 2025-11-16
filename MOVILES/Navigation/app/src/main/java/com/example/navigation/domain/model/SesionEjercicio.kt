package com.example.navigation.domain.model

data class SesionEjercicio(
    val id: Int,
    val sesionId: Int,
    val ejercicioId: Int,
    val nombreEjercicio: String,
    val orden: Int,
    val volumenKg: Double,
    val notas: String,
    val series: Int = 0,
    val repeticiones: Int = 0
)
