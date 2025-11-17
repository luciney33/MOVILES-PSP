package com.example.navigation.ui.sesion.DetalleSesion

import com.example.navigation.domain.model.Sesion
import com.example.navigation.domain.model.SesionEjercicio

data class DetalleSesionState(
    val isLoading: Boolean = false,
    val sesion: Sesion? = null,
    val ejercicios: List<SesionEjercicio> = emptyList(),
    val entrenamientoNombre: String = "",
    val error: String? = null
)