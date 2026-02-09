package com.example.navigationcompose.ui.screens.register

sealed interface RegisterEvent {
    data class UsernameChanged(val username: String) : RegisterEvent
    data class EmailChanged(val email: String) : RegisterEvent
    data class NombreChanged(val nombre: String) : RegisterEvent
    data class PasswordChanged(val password: String) : RegisterEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent
    data object Register : RegisterEvent
}

