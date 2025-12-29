package com.example.appcomposelucia.ui.common

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
}