package com.example.appdragonballapi.ui.pantallaListaCharacters

// Intenciones del usuario
sealed interface DragonBallIntent {
    data object LoadCharacters : DragonBallIntent
    data class LoadPage(val page: Int) : DragonBallIntent
    data class SearchCharacters(val name: String) : DragonBallIntent
   // data object ClearError : RickMortyIntent
}