package com.example.navigationcompose.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.data.repository.GymRepository
import com.example.navigationcompose.data.security.CryptoManager
import com.example.navigationcompose.data.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val gymRepository: GymRepository,
    private val cryptoManager: CryptoManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    fun logout(onLogoutFinished: () -> Unit) {
        viewModelScope.launch {
            gymRepository.logout()
            sessionManager.currentUser.value?.username?.let { cryptoManager.clearAllKeys(it) }
            sessionManager.clearPassword()
            onLogoutFinished()
        }
    }
}
