package com.example.navigationcompose.ui.screens.secretos.crear

data class CrearSecretoState(
    val contenido: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
