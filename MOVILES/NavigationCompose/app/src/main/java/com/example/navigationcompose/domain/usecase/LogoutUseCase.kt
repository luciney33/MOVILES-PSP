package com.example.navigationcompose.domain.usecase

import com.example.navigationcompose.data.repository.GymRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val gymRepository: GymRepository
) {
    operator fun invoke() {
        gymRepository.logout()
    }
}

