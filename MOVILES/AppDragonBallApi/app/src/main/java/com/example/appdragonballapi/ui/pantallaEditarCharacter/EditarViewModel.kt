package com.example.appdragonballapi.ui.pantallaEditarCharacter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdragonballapi.common.Constantes
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.appdragonballapi.domain.usecase.GetCharacterById
import com.example.appdragonballapi.domain.usecase.UpdateCharacter
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
class EditarViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterById,
    private val updateCharacterUseCase: UpdateCharacter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarUiState())
    val uiState: StateFlow<EditarUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: EditarIntent) {
        when (intent) {
            is EditarIntent.LoadCharacter -> loadCharacter(intent.id)
            is EditarIntent.UpdateCharacter -> updateCharacter(intent.character)
        }
    }

    private fun loadCharacter(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getCharacterByIdUseCase(id)
            when (result) {
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, character = result.data) }
                }
            }
        }
    }

    private fun updateCharacter(character: DragonBallCharacter) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = updateCharacterUseCase(character.id, character)
            when (result) {
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(Constantes.PERSONAJE_ACTUALIZADO_EXITOSO))
                    _events.send(UiEvent.NavigateBack)
                }
            }
        }
    }
}
