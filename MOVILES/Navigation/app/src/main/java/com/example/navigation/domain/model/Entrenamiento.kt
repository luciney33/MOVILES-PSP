package com.example.navigation.domain.model

data class Entrenamiento(
    val id: Int = 0,
    val nombre: String="",
    val descripcion: String="",
    val duracionMin: Int=0,
    val ejercicios: List<Ejercicio> = emptyList()
)
