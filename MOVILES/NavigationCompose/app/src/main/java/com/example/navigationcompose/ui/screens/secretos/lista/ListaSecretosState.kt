package com.example.navigationcompose.ui.screens.secretos.lista

import com.example.navigationcompose.domain.model.Secreto

data class ListaSecretosState(
    val secretos: List<Secreto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

