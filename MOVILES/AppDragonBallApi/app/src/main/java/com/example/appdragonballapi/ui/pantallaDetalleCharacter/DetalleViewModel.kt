package com.example.appdragonballapi.ui.pantallaDetalleCharacter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdragonballapi.domain.usecase.GetCharacterById
import com.example.appdragonballapi.ui.common.UiEvent
import com.example.appdragonballapi.common.NetworkResult
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
class DetalleViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterById,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        savedStateHandle.get<Int>("characterId")?.let {
            handleIntent(DetalleIntent.LoadCharacter(it))
        }
    }

    fun handleIntent(intent: DetalleIntent) {
        when (intent) {
            is DetalleIntent.LoadCharacter -> {
                loadCharacter(intent.id)
            }
        }
    }

    private fun loadCharacter(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getCharacterByIdUseCase(id)
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            character = result.data,
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
            }
        }
    }
}
