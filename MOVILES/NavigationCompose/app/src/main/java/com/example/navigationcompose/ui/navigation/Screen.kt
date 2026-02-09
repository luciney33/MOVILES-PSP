package com.example.navigationcompose.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Login : Screen
    @Serializable
    data object Register : Screen
    @Serializable
    data object Home : Screen

    @Serializable
    data object ListaEntrenamiento : Screen
    @Serializable
    data class DetalleEntrenamiento(val id: Long) : Screen
    @Serializable
    data object ApiExterna : Screen


}