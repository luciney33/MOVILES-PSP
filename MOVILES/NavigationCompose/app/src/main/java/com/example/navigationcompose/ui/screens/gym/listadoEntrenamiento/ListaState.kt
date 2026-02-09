package com.example.navigationcompose.ui.screens.gym.listadoEntrenamiento

import com.example.navigationcompose.domain.model.Entrenamiento

data class ListaState(
    val entrenamientos: List<Entrenamiento> = emptyList(),
    val isLoading: Boolean = false
)