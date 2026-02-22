package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import com.example.navigationcompose.domain.model.Secreto
import javax.inject.Inject

class CrearSecretoUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(
        contenidoPlano: String,
        passwordUsuario: String,
        receptorId: Long? = null
    ): NetworkResult<Secreto> {
        val crearResult = secretosRepository.crearSecreto(
            contenidoPlano = contenidoPlano,
            passwordUsuario = passwordUsuario
        )

        return when (crearResult) {
            is NetworkResult.Success -> {
                val secretoCreado = crearResult.data

                if (receptorId != null) {
                    val compartirResult = secretosRepository.compartirSecreto(
                        secretoId = secretoCreado.id,
                        receptorId = receptorId,
                        passwordUsuario = passwordUsuario
                    )

                    when (compartirResult) {
                        is NetworkResult.Success -> NetworkResult.Success(secretoCreado)
                        is NetworkResult.Error -> NetworkResult.Error("Secreto creado pero error al compartir: ${compartirResult.message}")
                    }
                } else {
                    NetworkResult.Success(secretoCreado)
                }
            }
            is NetworkResult.Error -> crearResult
        }
    }
}
