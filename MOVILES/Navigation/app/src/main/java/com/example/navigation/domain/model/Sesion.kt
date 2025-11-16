package com.example.navigation.domain.model

data class Sesion(
    val id: Int,
    val entrenamientoId: Int,
    val fechaInicio: Long,
    val duracionMs: Long,
    val ejerciciosCompletados: Int,
    val notas: String
)
