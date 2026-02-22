package com.example.navigationcompose.domain.usecase.dragonBall

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.DragonBallRepository
import com.example.navigationcompose.domain.model.DragonBallCharacter
import com.example.navigationcompose.domain.model.DragonBallValidator
import jakarta.inject.Inject

class GetAllCharacters @Inject constructor(private val repository: DragonBallRepository) {

    suspend operator fun invoke(page: Int): NetworkResult<List<DragonBallCharacter>> =
        DragonBallValidator.isPageNotNegative(page)
            .then { DragonBallValidator.isPageLimit(page) }
            .then {
                repository.getCharacters(page)
            }


}
