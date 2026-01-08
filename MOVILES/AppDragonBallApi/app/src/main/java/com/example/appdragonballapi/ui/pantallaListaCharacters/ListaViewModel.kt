package com.example.appdragonballapi.ui.pantallaListaCharacters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.appdragonballapi.domain.usecase.GetAllCharacters
import com.example.appdragonballapi.domain.usecase.DeleteCharacter
import com.example.appdragonballapi.common.Constantes
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
class ListaViewModel @Inject constructor(
    private val getCharactersUseCase: GetAllCharacters,
    private val deleteCharacterUseCase: DeleteCharacter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
       handleIntent(DragonBallIntent.LoadCharacters)
    }

    fun handleIntent(intent: DragonBallIntent) {
        when (intent) {
            is DragonBallIntent.LoadCharacters -> loadCharacters(1)
            is DragonBallIntent.LoadPage -> loadCharacters(intent.page)
            is DragonBallIntent.DeleteCharacter -> deleteCharacter(intent.id)
        }
    }


    private fun deleteCharacter(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = deleteCharacterUseCase(id)
            when (result) {
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(Constantes.PERSONAJE_ELIMINADO_EXITOSO))
                    loadCharacters(page = 1)
                }
            }
        }
    }

    private fun loadCharacters(page: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading= true) }

            val result = getCharactersUseCase(page)
            when (result)
            {
                is NetworkResult.Error -> {
                    _uiState.update { it.copy( isLoading= false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success ->
                    _uiState.update { it.copy( characters = result.data, isLoading= false) }
            }
        }
    }
}
