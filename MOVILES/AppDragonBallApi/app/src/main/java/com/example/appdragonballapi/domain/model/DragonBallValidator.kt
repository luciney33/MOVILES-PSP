package com.example.appdragonballapi.domain.model

import com.example.appdragonballapi.common.Constantes
import com.example.navigationhiltroom.common.NetworkResult



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
