package com.example.navigationcompose.data.security

import android.util.Log
import com.example.navigationcompose.data.remote.entity.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val _userPassword = MutableStateFlow<String?>(null)
    val userPassword: StateFlow<String?> = _userPassword.asStateFlow()

    private val _currentUser = MutableStateFlow<UsuarioEntity?>(null)
    val currentUser: StateFlow<UsuarioEntity?> = _currentUser.asStateFlow()

    fun savePasswordInSession(password: String) {
        _userPassword.value = password
        Log.d("SessionManager", "assword guardada en sesión: ${password.take(3)}***")
    }

    fun getPassword(): String? {
        val password = _userPassword.value
        Log.d("SessionManager", "Obteniendo password de sesión: ${if (password != null) "${password.take(3)}***" else "NULL"}")
        return password
    }

    fun hasPassword(): Boolean {
        return _userPassword.value != null
    }

    fun clearPassword() {
        _userPassword.value = null
        Log.d("SessionManager", "Password limpiada de sesión")
    }

    fun setCurrentUser(user: UsuarioEntity?) {
        _currentUser.value = user
    }

    fun getCurrentUser(): UsuarioEntity? {
        return _currentUser.value
    }

    fun clearSession() {
        _userPassword.value = null
        _currentUser.value = null
        Log.d("SessionManager", "Sesión limpiada")
    }
}
