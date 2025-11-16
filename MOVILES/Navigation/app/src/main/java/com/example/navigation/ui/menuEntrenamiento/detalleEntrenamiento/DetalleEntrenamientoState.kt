package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

import com.example.navigation.domain.model.SesionEjercicio

data class DetalleEntrenamientoState(
    val ejercicio: SesionEjercicio? = null,
    val mensaje: String? = null,
    val guardado: Boolean = false
)
