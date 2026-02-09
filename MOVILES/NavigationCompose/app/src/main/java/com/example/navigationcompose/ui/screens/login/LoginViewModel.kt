package com.example.navigationcompose.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.local.TokenManager
import com.example.navigationcompose.domain.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                _state.update { it.copy(username = event.username, error = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, error = null) }
            }
            LoginEvent.Login -> {
                login()
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = loginUseCase(
                username = _state.value.username,
                password = _state.value.password
            )

            when (result) {
                is NetworkResult.Success -> {
                    // Guardar tokens
                    tokenManager.saveAccessToken(result.data.accessToken)
                    tokenManager.saveRefreshToken(result.data.refreshToken)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            error = null
                        )
                    }
                    _uiEvent.send(UiEvent.ShowSnackbar("Login exitoso"))
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

