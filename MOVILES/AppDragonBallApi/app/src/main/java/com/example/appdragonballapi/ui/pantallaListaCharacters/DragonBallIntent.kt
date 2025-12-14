package com.example.appdragonballapi.ui.pantallaListaCharacters

sealed interface DragonBallIntent {
    data object LoadCharacters : DragonBallIntent
    data class LoadPage(val page: Int) : DragonBallIntent
    data class DeleteCharacter(val id: Int) : DragonBallIntent
}
