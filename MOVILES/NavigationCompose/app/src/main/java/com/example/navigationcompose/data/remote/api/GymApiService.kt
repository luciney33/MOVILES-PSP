package com.example.navigationcompose.data.remote.api

import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.data.remote.entity.EjercicioEntity
import com.example.navigationcompose.data.remote.entity.EntrenamientoEntity
import com.example.navigationcompose.data.remote.entity.LoginRequest
import com.example.navigationcompose.data.remote.entity.LoginResponse
import com.example.navigationcompose.data.remote.entity.UpdatePublicKeyRequest
import com.example.navigationcompose.data.remote.entity.UsuarioEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GymApiService {
    @POST(Constantes.API_AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST(Constantes.API_AUTH_REGISTER)
    suspend fun register(@Body request: UsuarioEntity): Response<UsuarioEntity>

    @POST(Constantes.API_ENTRENAMIENTOS)
    suspend fun createEntrenamiento(@Body entrenamiento: EntrenamientoEntity): Response<EntrenamientoEntity>

    @PUT(Constantes.API_ENTRENAMIENTOS_ID)
    suspend fun updateEntrenamiento(@Path(Constantes.ID) id: Long, @Body entrenamiento: EntrenamientoEntity): Response<EntrenamientoEntity>

    @DELETE(Constantes.API_ENTRENAMIENTOS_ID)
    suspend fun deleteEntrenamiento(@Path(Constantes.ID) id: Long): Response<Unit>

    @GET(Constantes.API_ENTRENAMIENTOS)
    suspend fun getEntrenamientos(): Response<List<EntrenamientoEntity>>

    @GET(Constantes.API_ENTRENAMIENTOS_ID)
    suspend fun getEntrenamientoById(@Path(Constantes.ID) id: Long): Response<EntrenamientoEntity>

    @GET(Constantes.API_EJERCICIOS)
    suspend fun getEjercicios(): Response<List<EjercicioEntity>>

    @PUT(Constantes.API_USUARIOS_PUBLIC_KEY)
    suspend fun updatePublicKey(@Body request: UpdatePublicKeyRequest): Response<String>
}

