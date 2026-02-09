package com.example.navigationcompose.ui.screens.register

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val nombre: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterSuccessful: Boolean = false
)

