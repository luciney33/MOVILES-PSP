package com.example.appdragonballapi.ui.pantallaDetalleCharacter

sealed interface DetalleIntent {
    data class LoadCharacter(val id: Int) : DetalleIntent
}
