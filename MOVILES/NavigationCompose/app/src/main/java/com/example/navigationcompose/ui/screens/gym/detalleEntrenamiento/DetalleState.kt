package com.example.navigationcompose.ui.screens.gym.detalleEntrenamiento

import com.example.navigationcompose.domain.model.Ejercicio

data class DetalleState(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val isLoading: Boolean = false,
    val ejercicios: List<Ejercicio> = emptyList()
)