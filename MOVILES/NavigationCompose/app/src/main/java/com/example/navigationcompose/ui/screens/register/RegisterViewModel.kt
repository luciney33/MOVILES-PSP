package com.example.navigationcompose.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.remote.entity.UsuarioEntity
import com.example.navigationcompose.domain.usecase.RegisterUseCase
import com.example.navigationcompose.ui.common.UiEvent
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UsernameChanged -> {
                _state.update { it.copy(username = event.username, error = null) }
            }
            is RegisterEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, error = null) }
            }
            is RegisterEvent.NombreChanged -> {
                _state.update { it.copy(nombre = event.nombre, error = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, error = null) }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword, error = null) }
            }
            RegisterEvent.Register -> {
                register()
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            // Validaciones
            if (_state.value.password != _state.value.confirmPassword) {
                _state.update { it.copy(error = "Las contraseñas no coinciden") }
                _uiEvent.send(UiEvent.ShowError("Las contraseñas no coinciden"))
                return@launch
            }

            if (_state.value.password.length < 6) {
                _state.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
                _uiEvent.send(UiEvent.ShowError("La contraseña debe tener al menos 6 caracteres"))
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_state.value.email).matches()) {
                _state.update { it.copy(error = "Email inválido") }
                _uiEvent.send(UiEvent.ShowError("Email inválido"))
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            // Crear UsuarioEntity para el registro
            val usuario = UsuarioEntity(
                id = 0, // El servidor generará el ID
                username = _state.value.username,
                email = _state.value.email,
                nombre = _state.value.nombre,
                rol = "USER",
                activo = true,
                publicKey = null
            )

            val result = registerUseCase(usuario)

            when (result) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRegisterSuccessful = true,
                            error = null
                        )
                    }
                    _uiEvent.send(UiEvent.ShowSnackbar("Registro exitoso. Por favor, inicia sesión"))
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    _uiEvent.send(UiEvent.ShowError(result.message))
                }
            }
        }
    }
}

