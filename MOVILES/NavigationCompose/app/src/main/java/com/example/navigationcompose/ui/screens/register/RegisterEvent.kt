package com.example.navigationcompose.ui.screens.register

sealed interface RegisterEvent {
    data class UserChanged(
        val username: String? = null,
        val email: String? = null,
        val nombre: String? = null,
        val password: String? = null,
        val confirmPassword: String? = null
    ) : RegisterEvent
    data object Register : RegisterEvent
}

