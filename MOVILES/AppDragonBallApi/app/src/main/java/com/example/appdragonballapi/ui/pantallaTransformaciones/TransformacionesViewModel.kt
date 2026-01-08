package com.example.appdragonballapi.ui.pantallaTransformaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdragonballapi.domain.usecase.GetCharacterById
import com.example.appdragonballapi.common.NetworkResult
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
class TransformacionesViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterById
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransformacionesUiState())
    val uiState: StateFlow<TransformacionesUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: TransformacionesIntent) {
        when (intent) {
            is TransformacionesIntent.GetTransformations -> {
                getTransformations(intent.characterId)
            }
        }
    }

    private fun getTransformations(characterId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getCharacterByIdUseCase(characterId)
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            transformations = result.data.transformations,
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
