package com.example.navigationcompose.ui.screens.gym.listadoEntrenamiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.domain.usecase.gym.DeleteEntrenamientoUseCase
import com.example.navigationcompose.domain.usecase.gym.GetEntrenamientosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListaViewModel @Inject constructor(
    private val getEntrenamientosUseCase: GetEntrenamientosUseCase,
    private val deleteEntrenamientoUseCase: DeleteEntrenamientoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ListaState())
    val state = _state.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val res = getEntrenamientosUseCase()) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(entrenamientos = res.data, isLoading = false, error = null) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = res.message) }
                }
            }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (deleteEntrenamientoUseCase(id)) {
                is NetworkResult.Success -> {
                    cargar()
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = "Error al eliminar el entrenamiento") }
                }
            }
        }
    }
}

