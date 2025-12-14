package com.example.appdragonballapi.ui.common


sealed interface UiEvent {
    data class ShowError(val message: String) : UiEvent
    data class ShowSnackbar(val message: String) : UiEvent
    data object NavigateBack : UiEvent
}