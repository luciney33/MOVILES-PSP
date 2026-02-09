package com.example.navigationcompose.ui.screens.login

sealed interface LoginEvent {
    data class UsernameChanged(val v: String) : LoginEvent
    data class PasswordChanged(val v: String) : LoginEvent
    data object Login : LoginEvent
}
