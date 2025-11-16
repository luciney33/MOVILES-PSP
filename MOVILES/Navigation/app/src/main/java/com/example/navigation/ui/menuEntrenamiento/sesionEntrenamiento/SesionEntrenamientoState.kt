package com.example.navigation.ui.menuEntrenamiento.sesionEntrenamiento

import com.example.navigation.domain.model.Sesion
import com.example.navigation.domain.model.SesionEjercicio

data class SesionEntrenamientoState(
    val sesion: Sesion? = null,
    val ejercicios: List<SesionEjercicio> = emptyList(),
    val mensaje: String? = null
)