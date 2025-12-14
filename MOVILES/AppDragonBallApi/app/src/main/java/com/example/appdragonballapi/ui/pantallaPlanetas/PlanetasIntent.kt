package com.example.appdragonballapi.ui.pantallaPlanetas

sealed interface PlanetasIntent {
    data object LoadPlanets : PlanetasIntent
}
