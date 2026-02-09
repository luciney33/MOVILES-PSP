package com.example.navigationcompose.data

import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.local.TokenManager
import com.example.navigationcompose.domain.model.Ejercicio
import com.example.navigationcompose.domain.model.Entrenamiento
import com.example.navigationcompose.domain.model.Usuario
import com.example.navigationcompose.data.remote.api.GymApiService
import com.example.navigationcompose.data.remote.entity.LoginRequest
import com.example.navigationcompose.data.remote.entity.LoginResponse
import com.example.navigationcompose.data.remote.entity.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymRepository @Inject constructor(
    private val apiService: GymApiService,
    private val tokenManager: TokenManager
) {


    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                tokenManager.saveToken(loginResponse.accessToken)
                NetworkResult.Success(loginResponse)
            } else {
                NetworkResult.Error("Credenciales incorrectas")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Error de red: ${e.message}")
        }
    }

    suspend fun register(usuario: UsuarioEntity): NetworkResult<Usuario> {
        return try {
            val response = apiService.register(usuario)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.toDomain())
            } else {
                NetworkResult.Error("Error en el registro")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Fallo de conexión")
        }
    }

    fun logout() {
        tokenManager.deleteToken()
    }


    suspend fun getEntrenamientos(): NetworkResult<List<Entrenamiento>> {
        return try {
            val response = apiService.getEntrenamientos()
            if (response.isSuccessful) {
                val list = response.body()?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(list)
            } else {
                NetworkResult.Error("Error al obtener entrenamientos: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun getEntrenamientoById(id: Long): NetworkResult<Entrenamiento> {
        return try {
            val response = apiService.getEntrenamientoById(id)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.toDomain())
            } else {
                NetworkResult.Error("Entrenamiento no encontrado")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Error")
        }
    }

    suspend fun getEjercicios(): NetworkResult<List<Ejercicio>> {
        return try {
            val response = apiService.getEjercicios()
            if (response.isSuccessful) {
                val list = response.body()?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(list)
            } else {
                NetworkResult.Error("Error al cargar ejercicios")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Error")
        }
    }

}