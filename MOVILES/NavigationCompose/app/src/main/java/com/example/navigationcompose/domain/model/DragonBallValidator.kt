package com.example.navigationcompose.domain.model

import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult

object DragonBallValidator {
    fun isPageNotNegative(page: Int): NetworkResult<Boolean> {
        return if (page < 0) NetworkResult.Error(Constantes.ERROR_PAGE_NUMBER) else NetworkResult.Success(
            true
        )
    }

    fun isPageLimit(page: Int): NetworkResult<Boolean> {
        return if (page > 12) NetworkResult.Error(Constantes.ERROR_PAGE_NUMBER) else NetworkResult.Success(
            true
        )
    }
}
