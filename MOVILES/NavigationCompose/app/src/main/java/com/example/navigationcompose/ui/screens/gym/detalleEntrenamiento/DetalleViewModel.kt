package com.example.navigationcompose.ui.screens.gym.detalleEntrenamiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.GymRepository
import com.example.navigationcompose.data.remote.entity.EntrenamientoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val repo: GymRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DetalleState(id = 0L))
    val state = _state.asStateFlow()

    fun cargarDetalle(id: Long) {
        viewModelScope.launch {
            val res = repo.getEntrenamientoById(id)
            if (res is NetworkResult.Success) {
                _state.update { it.copy(
                    nombre = res.data.nombre,
                    descripcion = res.data.descripcion,
                    ejercicios = res.data.ejercicios
                ) }
            }
        }
    }

    fun onNombreChange(v: String) = _state.update { it.copy(nombre = v) }
    fun onDescChange(v: String) = _state.update { it.copy(descripcion = v) }

    fun guardar() {
        viewModelScope.launch {
            val entity = EntrenamientoEntity(
                _state.value.id,
                1,
                _state.value.nombre,
                _state.value.descripcion
            )
            repo.saveEntrenamiento(entity)
        }
    }
}