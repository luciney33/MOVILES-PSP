package com.example.navigationcompose.domain.usecase

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import com.example.navigationcompose.data.remote.entity.LoginRequest
import com.example.navigationcompose.data.remote.entity.LoginResponse
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    suspend operator fun invoke(username: String, password: String): NetworkResult<LoginResponse> {
        return gymRepository.login(LoginRequest(username, password))
    }
}

