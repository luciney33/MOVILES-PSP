package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import com.example.navigationcompose.domain.model.SecretoDescifrado
import javax.inject.Inject

/**
 * Caso de uso para descifrar y verificar un secreto.
 *
 * Flujo de descifrado:
 * 1. Obtiene el secreto cifrado del servidor
 * 2. Carga la clave privada del usuario (requiere contraseña)
 * 3. Descifra la clave AES usando RSA con la clave privada
 * 4. Descifra el contenido usando AES-GCM con la clave AES
 * 5. Obtiene la clave pública del autor
 * 6. Verifica la firma digital del contenido
 * 7. Verifica el certificado del autor
 *
 * Seguridad:
 * - Solo el usuario con la clave privada correcta puede descifrar
 * - La contraseña incorrecta produce un error
 * - La firma inválida se marca en firmaValida = false
 * - El certificado inválido produce un error
 *
 * @param secretoId ID del secreto a descifrar
 * @param passwordUsuario Contraseña para descifrar la clave privada
 * @return NetworkResult<SecretoDescifrado> con contenido plano y validación de firma
 */
class DescifrarSecretoUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(
        secretoId: Long,
        passwordUsuario: String
    ): NetworkResult<SecretoDescifrado> {
        return secretosRepository.descifrarSecreto(
            secretoId = secretoId,
            passwordUsuario = passwordUsuario
        )
    }
}

