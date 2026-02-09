package com.example.navigationcompose.domain.usecase

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import com.example.navigationcompose.data.remote.entity.UsuarioEntity
import com.example.navigationcompose.domain.model.Usuario
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    suspend operator fun invoke(usuario: UsuarioEntity): NetworkResult<Usuario> {
        return gymRepository.register(usuario)
    }
}

