package com.example.appdragonballapi.ui.pantallaListaCharacters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.appdragonballapi.domain.usecase.GetAllCharacters
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
            is DragonBallIntent.LoadCharacters -> loadCharacters(1,null)
            is DragonBallIntent.LoadPage -> loadCharacters(intent.page,null)
//            is RickMortyIntent.ClearError -> clearError()
            is DragonBallIntent.SearchCharacters -> TODO()
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
                    _events.send(UiEvent.ShowSnackbar(result.message ?: "Unknown Error"))
                }
                is NetworkResult.Success ->
                    _uiState.update { it.copy( characters = result.data  , isLoading= false) }
            }


        }
    }

}

