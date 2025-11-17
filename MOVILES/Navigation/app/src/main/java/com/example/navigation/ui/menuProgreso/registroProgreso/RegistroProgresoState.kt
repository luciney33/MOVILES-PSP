package com.example.navigation.ui.menuProgreso.registroProgreso


data class RegistroProgresoState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)
