package com.example.navigationcompose.ui.screens.gym.listadoEntrenamiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListaViewModel @Inject constructor(private val repo: GymRepository) : ViewModel() {
    private val _state = MutableStateFlow(ListaState())
    val state = _state.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val res = repo.getEntrenamientos()
            if (res is NetworkResult.Success) _state.update { it.copy(entrenamientos = res.data, isLoading = false) }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            repo.deleteEntrenamiento(id)
            cargar()
        }
    }
}