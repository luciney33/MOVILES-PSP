package com.example.navigationcompose.ui.screens.gym.detalleEntrenamiento

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.remote.entity.EntrenamientoEntity
import com.example.navigationcompose.data.remote.entity.toEntity
import com.example.navigationcompose.domain.usecase.gym.GetEntrenamientoByIdUseCase
import com.example.navigationcompose.domain.usecase.gym.SaveEntrenamientoUseCase
import com.example.navigationcompose.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val getEntrenamientoByIdUseCase: GetEntrenamientoByIdUseCase,
    private val saveEntrenamientoUseCase: SaveEntrenamientoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Long = savedStateHandle.toRoute<Screen.DetalleEntrenamiento>().id
    private val _state = MutableStateFlow(DetalleState(id = id))
    val state = _state.asStateFlow()

    init {
        if (id != 0L) cargarDetalle()
    }

    fun cargarDetalle() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val res = getEntrenamientoByIdUseCase(id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(
                        nombre = res.data.nombre,
                        descripcion = res.data.descripcion,
                        ejercicios = res.data.ejercicios,
                        isLoading = false,
                        error = null
                    ) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = res.message ?: Constantes.ERROR_CARGAR_ENTRENAMIENTO
                    ) }
                }
            }
        }
    }

    fun onNombreChange(v: String) = _state.update { it.copy(nombre = v) }
    fun onDescChange(v: String) = _state.update { it.copy(descripcion = v) }

    fun guardar() {
        viewModelScope.launch {
            if (_state.value.nombre.isBlank()) {
                _state.update { it.copy(error = Constantes.ERROR_NOMBRE_VACIO) }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }
            val entity = EntrenamientoEntity(
                id = _state.value.id,
                usuarioId = 1,
                nombre = _state.value.nombre,
                descripcion = _state.value.descripcion,
                ejercicios = _state.value.ejercicios.map { it.toEntity() } // ✅ Convertir a Entity
            )

            when (val result = saveEntrenamientoUseCase(entity)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, saveSuccess = true, error = null) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.message ?: Constantes.ERROR_GUARDAR_ENTRENAMIENTO
                    ) }
                }
            }
        }
    }
}

