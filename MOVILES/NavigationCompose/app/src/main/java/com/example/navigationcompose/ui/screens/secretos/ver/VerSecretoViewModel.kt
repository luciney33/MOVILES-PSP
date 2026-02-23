package com.example.navigationcompose.ui.screens.secretos.ver

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.security.SessionManager
import com.example.navigationcompose.domain.usecase.secretos.DescifrarSecretoUseCase
import com.example.navigationcompose.domain.usecase.secretos.EliminarSecretoUseCase
import com.example.navigationcompose.domain.usecase.secretos.RevocarAccesoSecretoUseCase
import com.example.navigationcompose.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerSecretoViewModel @Inject constructor(
    private val descifrarSecretoUseCase: DescifrarSecretoUseCase,
    private val eliminarSecretoUseCase: EliminarSecretoUseCase,
    private val revocarAccesoSecretoUseCase: RevocarAccesoSecretoUseCase,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val secretoId: Long = savedStateHandle.toRoute<Screen.VerSecreto>().id
    private val _state = MutableStateFlow(VerSecretoState())
    val state = _state.asStateFlow()

    init {
        descifrar()
    }

    fun descifrar() {
        val password = sessionManager.getPassword()

        if (password == null) {
            Log.e("VerSecretoViewModel", "Password es NULL en sesión")
            _state.update {
                it.copy(
                    isLoading = false,
                    error = """
                        Sesión expirada
                        
                        Por favor, cierra sesión y vuelve a hacer login.
                        
                        Esto es necesario para que el sistema guarde tu contraseña en memoria y puedas descifrar los secretos.
                    """.trimIndent()
                )
            }
            return
        }

        Log.d("VerSecretoViewModel", "Password obtenida de sesión, descifrando secreto...")

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = descifrarSecretoUseCase(secretoId, password, _state.value.autorId)) {
                is NetworkResult.Success -> {
                    Log.d("VerSecretoViewModel", " Secreto descifrado correctamente")
                    _state.update { it.copy(secretoDescifrado = result.data, isLoading = false, error = null) }
                }
                is NetworkResult.Error -> {
                    Log.e("VerSecretoViewModel", "Error al descifrar: ${result.message}")
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun eliminarSecreto() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (eliminarSecretoUseCase(secretoId)) {
                is NetworkResult.Success<Unit> -> {
                    _state.update { it.copy(isLoading = false, deleteSuccess = true) }
                }
                is NetworkResult.Error<Unit> -> {
                    _state.update { it.copy(isLoading = false, error = Constantes.ERROR_ELIMINAR_SECRETO) }
                }
            }
        }
    }

    fun revocarAcceso(usuarioId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (revocarAccesoSecretoUseCase(secretoId, usuarioId)) {
                is NetworkResult.Success<Unit> -> {
                    descifrar()
                }
                is NetworkResult.Error<Unit> -> {
                    _state.update { it.copy(isLoading = false, error = Constantes.ERROR_REVOCAR_ACCESO) }
                }
            }
        }
    }
}
