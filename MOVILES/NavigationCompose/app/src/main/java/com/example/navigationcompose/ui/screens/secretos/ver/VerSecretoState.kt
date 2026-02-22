package com.example.navigationcompose.ui.screens.secretos.ver

import com.example.navigationcompose.domain.model.SecretoDescifrado

data class VerSecretoState(
    val secretoDescifrado: SecretoDescifrado? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteSuccess: Boolean = false
)

