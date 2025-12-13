package com.example.appdragonballapi.domain.usecase

import com.example.appdragonballapi.data.DragonBallRepository
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.appdragonballapi.domain.model.DragonBallValidator
import com.example.navigationhiltroom.common.NetworkResult
import jakarta.inject.Inject

class GetAllCharacters @Inject constructor(private val repository: DragonBallRepository) {

    suspend operator fun invoke(page: Int, name : String?): NetworkResult<List<DragonBallCharacter>> =
        DragonBallValidator.isPageNotNegative(page)
            .then { DragonBallValidator.isPageLimit(page) }
            .then { DragonBallValidator.isNameValidIfPresent(name) }
            .then {
                repository.getCharacters(page, name)
            }


}
