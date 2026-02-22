package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.security.CryptoManager
import javax.inject.Inject

class GetClavePublicaUseCase @Inject constructor(
    private val cryptoManager: CryptoManager
) {
    suspend operator fun invoke(): NetworkResult<String> {
        return try {
            val clavePublicaBase64 = cryptoManager.getPublicKeyBase64()
            NetworkResult.Success(clavePublicaBase64)
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_OBTENER_CLAVE_PUBLICA}${e.message}")
        }
    }
}

