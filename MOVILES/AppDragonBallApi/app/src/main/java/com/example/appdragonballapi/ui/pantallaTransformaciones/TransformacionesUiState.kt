package com.example.appdragonballapi.ui.pantallaTransformaciones

import com.example.appdragonballapi.domain.model.Transformation

data class TransformacionesUiState(
    val transformations: List<Transformation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
