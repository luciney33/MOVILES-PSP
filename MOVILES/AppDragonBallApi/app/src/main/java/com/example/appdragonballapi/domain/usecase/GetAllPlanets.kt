package com.example.appdragonballapi.domain.usecase

import com.example.appdragonballapi.data.DragonBallRepository
import com.example.appdragonballapi.domain.model.Planet
import com.example.appdragonballapi.common.NetworkResult
import jakarta.inject.Inject

class GetAllPlanets @Inject constructor(private val repository: DragonBallRepository) {

    suspend operator fun invoke(page: Int): NetworkResult<List<Planet>> = repository.getPlanets(page)

}