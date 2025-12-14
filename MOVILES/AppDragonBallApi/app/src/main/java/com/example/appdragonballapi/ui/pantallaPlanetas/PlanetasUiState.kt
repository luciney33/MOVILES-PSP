package com.example.appdragonballapi.ui.pantallaPlanetas

import com.example.appdragonballapi.domain.model.Planet

data class PlanetasUiState(
    val planets: List<Planet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
