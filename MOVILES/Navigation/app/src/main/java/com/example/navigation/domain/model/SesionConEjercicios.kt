package com.example.navigation.domain.model

data class SesionConEjercicios(
    val sesion: Sesion,
    val ejercicios: List<SesionEjercicio>
)

