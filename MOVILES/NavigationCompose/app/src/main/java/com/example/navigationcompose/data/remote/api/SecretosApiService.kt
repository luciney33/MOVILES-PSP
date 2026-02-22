package com.example.navigationcompose.data.remote.api

import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.data.remote.entity.*
import retrofit2.Response
import retrofit2.http.*

interface SecretosApiService {

    @GET(Constantes.API_USUARIOS)
    suspend fun getUsuarios(): Response<List<UsuarioSecretoDto>>

    @GET(Constantes.API_USUARIOS_ID)
    suspend fun getUsuarioById(@Path(Constantes.ID) usuarioId: Long): Response<UsuarioSecretoDto>

    @POST(Constantes.API_SECRETOS)
    suspend fun crearSecreto(@Body request: CrearSecretoRequest): Response<Long>

    @GET(Constantes.API_SECRETOS)
    suspend fun listarSecretos(): Response<List<SecretoSummaryDto>>

    @GET(Constantes.API_SECRETOS_ID)
    suspend fun getSecretoById(@Path(Constantes.ID) secretoId: Long): Response<SecretoCifradoResponse>

    @POST(Constantes.API_SECRETOS_COMPARTIR)
    suspend fun compartirSecreto(
        @Path(Constantes.ID) secretoId: Long,
        @Body request: CompartirSecretoRequest
    ): Response<Void>

    @DELETE(Constantes.API_SECRETOS_REVOCAR)
    suspend fun revocarAcceso(
        @Path("secretoId") secretoId: Long,
        @Path("usuarioId") usuarioId: Long
    ): Response<Void>

    @DELETE(Constantes.API_SECRETOS_ID)
    suspend fun eliminarSecreto(@Path(Constantes.ID) secretoId: Long): Response<Void>

    @GET(Constantes.API_CRYPTO_PUBLIC_KEY)
    suspend fun getPublicKeyServidor(): Response<ClavePublicaServidorResponse>
}

