package com.example.navigationcompose.domain.model

data class Ejercicio(
    val id: Long,
    val nombre: String,
    val tipo: String,
    val imageUrl: String,
    val descripcion: String
)

data class Entrenamiento(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val ejercicios: List<Ejercicio>
)

data class Usuario(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val rol: String
)