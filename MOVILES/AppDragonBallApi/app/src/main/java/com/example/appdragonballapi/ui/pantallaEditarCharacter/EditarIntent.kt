package com.example.appdragonballapi.ui.pantallaEditarCharacter

import com.example.appdragonballapi.domain.model.DragonBallCharacter

sealed interface EditarIntent {
    data class LoadCharacter(val id: Int) : EditarIntent
    data class UpdateCharacter(val character: DragonBallCharacter) : EditarIntent
}
