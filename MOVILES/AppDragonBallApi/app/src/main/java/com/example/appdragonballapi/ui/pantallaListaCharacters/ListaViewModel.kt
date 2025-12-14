package com.example.appdragonballapi.ui.pantallaListaCharacters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.appdragonballapi.domain.usecase.GetAllCharacters
import com.example.appdragonballapi.domain.usecase.DeleteCharacter
import com.example.appdragonballapi.domain.usecase.UpdateCharacter
import com.example.appdragonballapi.domain.usecase.AddCharacter
import com.example.appdragonballapi.common.Constantes
import com.example.navigationhiltroom.common.NetworkResult
import com.example.navigationhiltroom.ui.common.UiEvent


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
    private val deleteCharacterUseCase: DeleteCharacter,
    private val updateCharacterUseCase: UpdateCharacter,
    private val addCharacterUseCase: AddCharacter

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
            is DragonBallIntent.LoadCharacters -> loadCharacters(1, _uiState.value.searchQuery.ifBlank { null })
            is DragonBallIntent.LoadPage -> loadCharacters(intent.page, _uiState.value.searchQuery.ifBlank { null })
            is DragonBallIntent.SearchCharacters -> searchCharacters(intent.name)
            is DragonBallIntent.DeleteCharacter -> deleteCharacter(intent.id)
            is DragonBallIntent.AddCharacter -> addCharacter(intent.character)
            is DragonBallIntent.UpdateCharacter -> updateCharacter(intent.id, intent.character)
        }
    }
    
    private fun searchCharacters(name: String) {
        _uiState.update { it.copy(searchQuery = name) }
        loadCharacters(page = 1, name = name.ifBlank { null })
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
                    // Recargar la lista después de eliminar
                    loadCharacters(page = 1, name = _uiState.value.searchQuery.ifBlank { null })
                }
            }
        }
    }

    private fun updateCharacter(id: Int, character: com.example.appdragonballapi.domain.model.DragonBallCharacter) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = updateCharacterUseCase(id, character)
            when (result) {
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(UiEvent.ShowSnackbar(Constantes.PERSONAJE_ACTUALIZADO_EXITOSO))
                    // Recargar la lista después de actualizar
                    loadCharacters(page = 1, name = _uiState.value.searchQuery.ifBlank { null })
                }
            }
        }
    }

    private fun addCharacter(character: com.example.appdragonballapi.domain.model.DragonBallCharacter) {
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
                    // Recargar la lista después de agregar
                    loadCharacters(page = 1, name = _uiState.value.searchQuery.ifBlank { null })
                }
            }
        }
    }



    private fun loadCharacters(page: Int = 1,name: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading= true) }

            val result = getCharactersUseCase(page,name)
            when (result)
            {
                is NetworkResult.Error -> {
                    _uiState.update { it.copy( isLoading= false) }
                    _events.send(UiEvent.ShowSnackbar(result.message))
                }
                is NetworkResult.Success ->
                    _uiState.update { it.copy( characters = result.data  , isLoading= false) }
            }


        }
    }

}