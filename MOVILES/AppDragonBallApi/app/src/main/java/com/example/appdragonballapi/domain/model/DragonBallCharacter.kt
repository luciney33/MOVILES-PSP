package com.example.appdragonballapi.domain.model

data class DragonBallCharacter(
    val id: Int,
    val name: String,
    val ki: String,
    val race: String,
    val description: String,
    val imageUrl: String,
    val planet: Planet?,
    val transformations: List<Transformation>
)

data class Planet(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class Transformation(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val ki: String
)
