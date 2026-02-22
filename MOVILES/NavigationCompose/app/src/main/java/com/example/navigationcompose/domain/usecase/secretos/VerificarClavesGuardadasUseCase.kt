package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.data.security.CryptoManager
import javax.inject.Inject

/**
 * Caso de uso para verificar si el usuario ya tiene claves guardadas.
 *
 * Utilidad:
 * - Saber si hay que generar claves en el registro
 * - Verificar si el usuario puede usar funciones de secretos
 * - Mostrar advertencias si faltan claves
 *
 * @return true si existen claves guardadas, false si no
 */
class VerificarClavesGuardadasUseCase @Inject constructor(
    private val cryptoManager: CryptoManager
) {
    suspend operator fun invoke(): Boolean {
        return cryptoManager.hasStoredKeys()
    }
}

