package com.example.appdragonballapi.domain.usecase

import com.example.appdragonballapi.data.DragonBallRepository
import com.example.navigationhiltroom.common.NetworkResult
import jakarta.inject.Inject

class DeleteCharacter @Inject constructor(
    private val repository: DragonBallRepository
) {
    suspend operator fun invoke(id: Int): NetworkResult<Unit> {
        return repository.deleteCharacter(id)
    }
}

