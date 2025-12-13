package com.example.appdragonballapi.domain.usecase

import com.example.appdragonballapi.data.DragonBallRepository
import jakarta.inject.Inject

class GetCharacterById @Inject constructor(private val repository: DragonBallRepository) {
    suspend operator fun invoke(id: Int) =
        repository.getCharacterById(id)
}