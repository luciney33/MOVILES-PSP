package com.example.appdragonballapi.ui.pantallaCrearCharacter

import com.example.appdragonballapi.domain.model.DragonBallCharacter

sealed interface CrearIntent {
    data class AddCharacter(val character: DragonBallCharacter) : CrearIntent
    data class OnCajaChange(val name: String, val ki: String, val race: String, val description: String) : CrearIntent
}
