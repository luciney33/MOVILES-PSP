package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import javax.inject.Inject

/**
 * Caso de uso para compartir un secreto con otro usuario.
 *
 * Flujo de compartición (re-cifrado):
 * 1. Obtiene el secreto del servidor (tiene la clave AES cifrada con nuestra pública)
 * 2. Carga nuestra clave privada (requiere contraseña)
 * 3. Descifra la clave AES con nuestra clave privada (RSA)
 * 4. Obtiene la clave pública del nuevo destinatario
 * 5. Verifica el certificado del destinatario
 * 6. RE-CIFRA la clave AES con la clave pública del destinatario (RSA)
 * 7. Envía la clave AES re-cifrada al servidor
 *
 * IMPORTANTE:
 * - La clave privada NUNCA sale del dispositivo
 * - El contenido nunca se descifra en este proceso
 * - Solo se re-cifra la clave AES
 * - El servidor almacena múltiples versiones de la clave AES cifrada
 *   (una por cada usuario que tiene acceso)
 *
 * @param secretoId ID del secreto a compartir
 * @param receptorId ID del usuario con quien compartir
 * @param passwordUsuario Contraseña para descifrar nuestra clave privada
 */
class CompartirSecretoUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(
        secretoId: Long,
        receptorId: Long,
        passwordUsuario: String
    ): NetworkResult<Unit> {
        return secretosRepository.compartirSecreto(
            secretoId = secretoId,
            receptorId = receptorId,
            passwordUsuario = passwordUsuario
        )
    }
}

