package com.example.appdragonballapi.domain.model

data class ApiModel(
    val items: List<DragonBallCharacter>,
    val links: Links,
    val meta: Meta
)