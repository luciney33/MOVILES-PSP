package com.example.navigation.ui.menuProgreso.ejercicioProgreso

import com.example.navigation.domain.model.Progreso

data class EjercicioProgresoState(
    val items: List<Progreso> = emptyList(),
    val mensaje: String? = null
)
