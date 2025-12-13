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
    fun isNameValidIfPresent(name: String?): NetworkResult<Boolean> {
        return if (name.isNullOrBlank()) {
            NetworkResult.Success(true)
        } else if (name.length < 2) {
            NetworkResult.Error(Constantes.ERROR_NOMBRE_BUSQUEDA)
        } else {
            NetworkResult.Success(true)
        }
    }
}
