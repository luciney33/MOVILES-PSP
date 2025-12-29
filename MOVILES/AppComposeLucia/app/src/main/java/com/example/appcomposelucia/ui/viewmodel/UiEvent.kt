package com.example.appcomposelucia.ui.viewmodel

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}
