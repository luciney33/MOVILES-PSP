package com.example.appdragonballapi.ui.pantallaListaCharacters

import com.example.appdragonballapi.domain.model.DragonBallCharacter

// Estado persistente de la pantalla
data class ListaUiState(
    val characters: List<DragonBallCharacter> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)
