package com.example.appdragonballapi.ui.pantallaPlanetas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdragonballapi.domain.usecase.GetAllPlanets
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
class PlanetasViewModel @Inject constructor(
    private val getAllPlanetsUseCase: GetAllPlanets
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanetasUiState())
    val uiState: StateFlow<PlanetasUiState> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        handleIntent(PlanetasIntent.LoadPlanets)
    }

    fun handleIntent(intent: PlanetasIntent) {
        when (intent) {
            is PlanetasIntent.LoadPlanets -> {
                loadPlanets()
            }
        }
    }

    private fun loadPlanets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getAllPlanetsUseCase(1)
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            planets = result.data,
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
