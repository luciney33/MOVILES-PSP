package com.example.navigationcompose.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey{
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

    @Serializable
    data object SecretosMain : Screen
    @Serializable
    data object ListaSecretos : Screen
    @Serializable
    data object CrearSecreto : Screen
    @Serializable
    data class VerSecreto(val id: Long) : Screen
    @Serializable
    data class CompartirSecreto(val secretoId: Long) : Screen

}