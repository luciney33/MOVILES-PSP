package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.data.security.CryptoManager
import javax.inject.Inject

class VerificarClavesGuardadasUseCase @Inject constructor(
    private val cryptoManager: CryptoManager
) {
    suspend operator fun invoke(): Boolean {
        return cryptoManager.hasStoredKeys()
    }
}

