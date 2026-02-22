package com.example.navigationcompose.ui.screens.secretos.compartir

import com.example.navigationcompose.domain.model.Usuario

data class CompartirSecretoState(
    val usuarios: List<Usuario> = emptyList(),
    val usuariosSeleccionados: Set<Long> = emptySet(),
     val isLoading: Boolean = false,
    val isLoadingUsuarios: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

