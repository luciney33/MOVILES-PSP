package com.example.navigation.ui.home

import com.example.navigation.domain.model.SesionSummary

data class HomeState(
    val sesiones: List<SesionSummary> = emptyList()
)
