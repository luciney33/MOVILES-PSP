package com.example.navigationcompose.ui.screens.dragonBall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.domain.usecase.dragonBall.GetAllCharacters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListaViewModel @Inject constructor(
    private val getCharactersUseCase: GetAllCharacters
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    init {
        handleIntent(DragonBallIntent.LoadCharacters)
    }

    fun handleIntent(intent: DragonBallIntent) {
        when (intent) {
            is DragonBallIntent.LoadCharacters -> loadCharacters()
            else -> {}
        }
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = getCharactersUseCase(1)

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        characters = result.data,
                        isLoading = false
                    )}
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.message
                    )}
                }
            }
        }
    }
}