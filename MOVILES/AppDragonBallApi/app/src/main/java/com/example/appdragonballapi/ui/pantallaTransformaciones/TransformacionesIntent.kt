package com.example.appdragonballapi.ui.pantallaTransformaciones

sealed interface TransformacionesIntent {
    data class GetTransformations(val characterId: Int) : TransformacionesIntent
}
