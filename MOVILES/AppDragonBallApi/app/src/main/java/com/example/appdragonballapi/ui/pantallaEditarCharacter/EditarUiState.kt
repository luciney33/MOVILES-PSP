package com.example.appdragonballapi.ui.pantallaEditarCharacter

import com.example.appdragonballapi.domain.model.DragonBallCharacter

data class EditarUiState(
    val character: DragonBallCharacter? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
