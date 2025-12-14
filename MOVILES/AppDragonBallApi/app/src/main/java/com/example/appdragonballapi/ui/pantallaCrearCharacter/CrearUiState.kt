package com.example.appdragonballapi.ui.pantallaCrearCharacter

data class CrearUiState(
    val name: String = "",
    val ki: String = "",
    val race: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
