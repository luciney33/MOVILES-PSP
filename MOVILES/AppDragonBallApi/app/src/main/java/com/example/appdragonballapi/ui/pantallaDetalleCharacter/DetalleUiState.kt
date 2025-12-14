package com.example.appdragonballapi.ui.pantallaDetalleCharacter

import com.example.appdragonballapi.domain.model.DragonBallCharacter

data class DetalleUiState(
    val character: DragonBallCharacter? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
