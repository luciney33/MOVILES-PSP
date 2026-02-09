package com.example.navigationcompose.ui.screens.login

sealed interface LoginEvent {
    data class UsernameChanged(val username: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    data object Login : LoginEvent
}

