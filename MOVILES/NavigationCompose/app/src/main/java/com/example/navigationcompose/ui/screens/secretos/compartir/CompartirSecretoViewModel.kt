package com.example.navigationcompose.ui.screens.secretos.compartir

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.security.SessionManager
import com.example.navigationcompose.domain.model.Usuario
import com.example.navigationcompose.domain.usecase.secretos.CompartirSecretoUseCase
import com.example.navigationcompose.domain.usecase.secretos.ObtenerUsuariosUseCase
import com.example.navigationcompose.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompartirSecretoViewModel @Inject constructor(
    private val compartirSecretoUseCase: CompartirSecretoUseCase,
    private val obtenerUsuariosUseCase: ObtenerUsuariosUseCase,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val secretoId: Long = savedStateHandle.toRoute<Screen.CompartirSecreto>().secretoId
    private val _state = MutableStateFlow(CompartirSecretoState())
    val state = _state.asStateFlow()

    init {
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingUsuarios = true) }
            when (val result = obtenerUsuariosUseCase()) {
                is NetworkResult.Success<List<Usuario>> -> {
                    _state.update { it.copy(usuarios = result.data, isLoadingUsuarios = false) }
                }
                is NetworkResult.Error<List<Usuario>> -> {
                    _state.update { it.copy(isLoadingUsuarios = false, error = result.message) }
                }
            }
        }
    }

    fun toggleUsuarioSeleccionado(usuarioId: Long) {
        _state.update { state ->
            val newSet = state.usuariosSeleccionados.toMutableSet()
            if (newSet.contains(usuarioId)) {
                newSet.remove(usuarioId)
            } else {
                newSet.add(usuarioId)
            }
            state.copy(usuariosSeleccionados = newSet, error = null)
        }
    }

    fun compartir() {
        val currentState = _state.value

        if (currentState.usuariosSeleccionados.isEmpty()) {
            _state.update { it.copy(error = "Debe seleccionar al menos un usuario") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Obtener la contraseña de la sesión
            val passwordUsuario = sessionManager.getPassword()
            if (passwordUsuario == null) {
                _state.update { it.copy(isLoading = false, error = "Error: Sesión expirada. Por favor, inicie sesión nuevamente.") }
                return@launch
            }

            android.util.Log.d("CompartirSecreto", "✅ Password obtenida de sesión, compartiendo secreto...")

            var hasError = false
            var errorMessage = ""

            for (receptorId in currentState.usuariosSeleccionados) {
                when (val result = compartirSecretoUseCase(
                    secretoId = secretoId,
                    receptorId = receptorId,
                    passwordUsuario = passwordUsuario
                )) {
                    is NetworkResult.Success<Unit> -> {
                        // Continuar con el siguiente usuario
                    }
                    is NetworkResult.Error<Unit> -> {
                        hasError = true
                        errorMessage = result.message
                        break
                    }
                }
            }

            if (hasError) {
                _state.update { it.copy(isLoading = false, error = errorMessage) }
            } else {
                _state.update { it.copy(isLoading = false, success = true) }
            }
        }
    }
    }

