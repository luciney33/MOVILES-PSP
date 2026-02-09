package com.example.navigationcompose.ui.screens.dragonBall

sealed interface DragonBallIntent {
    data object LoadCharacters : DragonBallIntent
}
