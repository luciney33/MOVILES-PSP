package com.example.appdragonballapi.ui.pantallaListaCharacters

import com.example.appdragonballapi.domain.model.DragonBallCharacter

// Intenciones del usuario
sealed interface DragonBallIntent {
    data object LoadCharacters : DragonBallIntent
    data class LoadPage(val page: Int) : DragonBallIntent
    data class SearchCharacters(val name: String) : DragonBallIntent

    data class DeleteCharacter(val id: Int) : DragonBallIntent

    data class AddCharacter(val character: DragonBallCharacter) : DragonBallIntent

    data class UpdateCharacter(val id: Int, val character: DragonBallCharacter) : DragonBallIntent

}