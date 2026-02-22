package com.example.navigationcompose.domain.usecase.secretos

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.repository.SecretosRepository
import com.example.navigationcompose.domain.model.Usuario
import javax.inject.Inject

class GetUsuariosParaCompartirUseCase @Inject constructor(
    private val secretosRepository: SecretosRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Usuario>> {
        return secretosRepository.getUsuarios()
    }
}

