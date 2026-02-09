package com.example.navigationcompose.data.remote.api

import com.example.navigationcompose.data.remote.entity.EjercicioEntity
import com.example.navigationcompose.data.remote.entity.EntrenamientoEntity
import com.example.navigationcompose.data.remote.entity.LoginRequest
import com.example.navigationcompose.data.remote.entity.LoginResponse
import com.example.navigationcompose.data.remote.entity.UsuarioEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GymApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: UsuarioEntity): Response<UsuarioEntity>

    @GET("api/entrenamientos")
    suspend fun getEntrenamientos(): Response<List<EntrenamientoEntity>>

    @GET("api/entrenamientos/{id}")
    suspend fun getEntrenamientoById(@Path("id") id: Long): Response<EntrenamientoEntity>

    @GET("api/ejercicios")
    suspend fun getEjercicios(): Response<List<EjercicioEntity>>
}