package com.example.navigation.domain.model



data class Serie(
    val id: Int ,
    val sesionEjercicioId: Int,
    val numero: Int,
    val pesoKg: Double,
    val repeticiones: Int,
    val completada: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

