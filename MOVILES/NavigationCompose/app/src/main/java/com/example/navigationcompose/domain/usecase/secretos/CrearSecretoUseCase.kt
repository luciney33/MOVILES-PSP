package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import com.example.navigationcompose.domain.model.Secreto
import javax.inject.Inject

/**
 * Caso de uso para crear un secreto cifrado end-to-end.
 *
 * Flujo completo de cifrado:
 * 1. Genera clave AES aleatoria para cifrar el contenido
 * 2. Cifra el contenido con AES-GCM
 * 3. Carga la clave privada del usuario (requiere contraseña)
 * 4. Firma el contenido cifrado con la clave privada
 * 5. Obtiene la clave pública del receptor
 * 6. Verifica el certificado del receptor
 * 7. Cifra la clave AES con la clave pública del receptor (RSA)
 * 8. Envía todo al servidor
 *
 * IMPORTANTE: El servidor nunca ve el contenido descifrado.
 *
 * @param contenidoPlano Texto del secreto (se cifrará)
 * @param receptorId ID del usuario que recibirá el secreto
 * @param passwordUsuario Contraseña para descifrar la clave privada del autor
 */
class CrearSecretoUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(
        contenidoPlano: String,
        receptorId: Long,
        passwordUsuario: String
    ): NetworkResult<Secreto> {
        return secretosRepository.crearSecreto(
            contenidoPlano = contenidoPlano,
            receptorId = receptorId,
            passwordUsuario = passwordUsuario
        )
    }
}

