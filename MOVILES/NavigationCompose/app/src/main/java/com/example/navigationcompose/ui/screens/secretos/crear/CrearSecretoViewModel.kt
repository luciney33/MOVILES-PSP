package com.example.navigationcompose.ui.screens.secretos.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.security.SessionManager
import com.example.navigationcompose.domain.usecase.secretos.CrearSecretoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrearSecretoViewModel @Inject constructor(
    private val crearSecretoUseCase: CrearSecretoUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(CrearSecretoState())
    val state = _state.asStateFlow()

    fun onContenidoChange(contenido: String) {
        _state.update { it.copy(contenido = contenido, error = null) }
    }

    fun crearSecreto() {
        val currentState = _state.value

        if (currentState.contenido.isBlank()) {
            _state.update { it.copy(error = "El contenido no puede estar vacío") }
            return
        }

        val password = sessionManager.getPassword()
        if (password == null) {
            _state.update { it.copy(error = "Sesión expirada. Por favor, vuelve a iniciar sesión") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = crearSecretoUseCase(
                contenidoPlano = currentState.contenido,
                passwordUsuario = password
            )) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, success = true) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}
