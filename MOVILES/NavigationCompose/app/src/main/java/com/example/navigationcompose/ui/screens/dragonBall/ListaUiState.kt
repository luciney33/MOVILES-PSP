package com.example.navigationcompose.ui.screens.dragonBall

import com.example.navigationcompose.domain.model.DragonBallCharacter

data class ListaUiState(
    val characters: List<DragonBallCharacter> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

