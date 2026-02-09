package com.example.navigationcompose.data.remote.entity

import com.example.navigationcompose.domain.model.DragonBallCharacter
import com.example.navigationcompose.domain.model.Planet
import com.example.navigationcompose.domain.model.Transformation
import com.example.navigationcompose.common.Constantes
import com.google.gson.annotations.SerializedName



data class DragonBallResponse(
    @SerializedName(Constantes.ITEMS)
    val characterEntities: List<CharacterEntity>,
    val links: Links,
    val meta: Meta
)

data class PlanetaResponse(
    @SerializedName(Constantes.ITEMS)
    val planetEntity: List<PlanetEntity>,
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

fun PlanetEntity.toDomain() = Planet(id, name, image)
fun TransformationEntity.toDomain() = Transformation(id, name, image, ki)

fun DragonBallCharacter.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        ki = this.ki,
        maxKi = this.ki,
        race = this.race,
        gender = "",
        description = this.description,
        image = this.imageUrl,
        affiliation = "",
        deletedAt = null,
        originPlanet = this.planet?.toEntity(),
        transformations = this.transformations.map { it.toEntity() }
    )
}

fun Planet.toEntity(): PlanetEntity {
    return PlanetEntity(
        id = this.id,
        name = this.name,
        isDestroyed = false,
        description = "",
        image = this.imageUrl,
        deletedAt = null
    )
}

fun Transformation.toEntity(): TransformationEntity {
    return TransformationEntity(
        id = this.id,
        name = this.name,
        image = this.imageUrl,
        ki = this.ki,
        deletedAt = null
    )
}
