package com.example.navigationcompose.data.remote.entity

import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.DragonBallCharacter
import com.google.gson.annotations.SerializedName


data class DragonBallResponse(
@SerializedName(Constantes.ITEMS)
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
    val image: String
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
        image = this.image
    )
}



