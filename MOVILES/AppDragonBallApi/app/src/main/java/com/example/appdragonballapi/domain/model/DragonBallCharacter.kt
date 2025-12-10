package com.example.appdragonballapi.domain.model

data class DragonBallCharacter(
    val id: Int,
    val name: String,
    val ki: String,
    val maxKi: String,
    val race: String,
    val gender: String,
    val description: String,
    val image: String,
    val affiliation: String,
    val deletedAt: Any
)

data class Transformation(
    val deletedAt: Any,
    val id: Int,
    val image: String,
    val ki: String,
    val name: String
)

data class DragonBallResponse(
    val results: List<DragonBallCharacter>
)