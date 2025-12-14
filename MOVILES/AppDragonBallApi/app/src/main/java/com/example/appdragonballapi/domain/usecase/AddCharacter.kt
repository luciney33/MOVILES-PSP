package com.example.appdragonballapi.domain.usecase

import com.example.appdragonballapi.data.DragonBallRepository
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.navigationhiltroom.common.NetworkResult
import jakarta.inject.Inject

class AddCharacter @Inject constructor(
    private val repository: DragonBallRepository
) {
    suspend operator fun invoke(character: DragonBallCharacter): NetworkResult<DragonBallCharacter> {
        return repository.addCharacter(character)
    }
}

