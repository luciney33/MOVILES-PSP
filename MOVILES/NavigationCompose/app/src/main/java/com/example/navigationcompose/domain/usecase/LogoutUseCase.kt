package com.example.navigationcompose.domain.usecase

import com.example.navigationcompose.data.repository.GymRepository
import com.example.navigationcompose.data.security.CryptoManager
import com.example.navigationcompose.data.security.SessionManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val gymRepository: GymRepository,
    private val cryptoManager: CryptoManager,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        sessionManager.getCurrentUser()?.username?.let {
            gymRepository.logout()
            cryptoManager.clearAllKeys(it)
            sessionManager.clearSession()
        }
    }
}
