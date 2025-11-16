package com.example.navigation.domain.model


data class EntrenamientoEjercicioRe(
    val entrenamientoId: Int,
    val ejercicioId: Int,
    val orden: Int,
    val series: Int,
    val repeticiones: Int,
    val descansoSeg: Int
)
