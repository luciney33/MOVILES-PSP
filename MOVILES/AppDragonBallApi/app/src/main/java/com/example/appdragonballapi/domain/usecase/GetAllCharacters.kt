package com.example.appdragonballapi.domain.usecase

import com.example.appdragonballapi.data.DragonBallRepository
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.appdragonballapi.domain.model.DragonBallValidator
import com.example.appdragonballapi.common.NetworkResult
import jakarta.inject.Inject

class GetAllCharacters @Inject constructor(private val repository: DragonBallRepository) {

    suspend operator fun invoke(page: Int): NetworkResult<List<DragonBallCharacter>> =
        DragonBallValidator.isPageNotNegative(page)
            .then { DragonBallValidator.isPageLimit(page) }
            .then {
                repository.getCharacters(page)
            }


}
