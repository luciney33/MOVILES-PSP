package com.example.appdragonballapi.ui.pantallaCrearCharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdragonballapi.common.Constantes
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.appdragonballapi.domain.usecase.AddCharacter
import com.example.navigationhiltroom.common.NetworkResult
import com.example.appdragonballapi.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrearViewModel @Inject constructor(
    private val addCharacterUseCase: AddCharacter
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearUiState())
    val uiState: StateFlow<CrearUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: CrearIntent) {
        when (intent) {
            is CrearIntent.AddCharacter -> addCharacter(intent.character)
            is CrearIntent.OnCajaChange -> onCajaChange(intent.name, intent.ki, intent.race, intent.description)
        }
    }

    private fun onCajaChange(name: String, ki: String, race: String, description: String) {
        _uiState.update { it.copy(name = name, ki = ki, race = race, description = description) }
    }

    private fun addCharacter(character: DragonBallCharacter) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = addCharacterUseCase(character)
            when (result) {
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(Constantes.PERSONAJE_AGREGADO_EXITOSO))
                    _events.send(UiEvent.NavigateBack)
                }
            }
        }
    }
}
