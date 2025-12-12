package com.example.appdragonballapi.data.remote.entity

import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.appdragonballapi.domain.model.Planet
import com.example.appdragonballapi.domain.model.Transformation

data class DragonBallEntity(
    val characterEntities: List<CharacterEntity>,
    val links: Links,
    val meta: Meta
)
data class CharacterEntity(
    val id: Int,
    val name: String,
    val ki: String,
    val maxKi: String,
    val race: String,
    val gender: String,
    val description: String,
    val image: String,
    val affiliation: String,
    val deletedAt: String?,
    val originPlanet: PlanetEntity?,
    val transformations: List<TransformationEntity>?
)
data class PlanetEntity(
    val id: Int,
    val name: String,
    val isDestroyed: Boolean,
    val description: String,
    val image: String,
    val deletedAt: String?
)

data class TransformationEntity(
    val id: Int,
    val name: String,
    val image: String,
    val ki: String,
    val deletedAt: String?
)
data class Links(
    val first: String,
    val last: String,
    val next: String,
    val previous: String
)
data class Meta(
    val currentPage: Int,
    val itemCount: Int,
    val itemsPerPage: Int,
    val totalItems: Int,
    val totalPages: Int
)

fun CharacterEntity.toDomain(): DragonBallCharacter {
    return DragonBallCharacter(
        id = this.id,
        name = this.name,
        ki = this.ki,
        race = this.race,
        description = this.description,
        imageUrl = this.image,
        planet = this.originPlanet?.toDomain(),
        transformations = this.transformations?.map { it.toDomain() } ?: emptyList()
    )
}

fun PlanetEntity.toDomain() = Planet(name, description, image)
fun TransformationEntity.toDomain() = Transformation(name, image, ki)