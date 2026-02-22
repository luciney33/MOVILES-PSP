package com.example.navigationcompose.data.remote.api

import com.example.navigationcompose.data.remote.entity.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service para operaciones de secretos cifrados end-to-end.
 *
 * Todos los endpoints requieren autenticación (Header: Authorization: Bearer {token})
 */
interface SecretosApiService {

    /**
     * Obtiene la lista de usuarios activos para compartir secretos.
     * Devuelve sus claves públicas y certificados.
     */
    @GET("/api/usuarios")
    suspend fun getUsuarios(): Response<List<UsuarioSecretoDto>>

    /**
     * Obtiene un usuario específico por ID.
     * Necesario para obtener su clave pública al compartir.
     */
    @GET("/api/usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") usuarioId: Long): Response<UsuarioSecretoDto>

    /**
     * Crea un nuevo secreto cifrado.
     * El contenido ya debe venir cifrado desde el cliente.
     */
    @POST("/api/secretos")
    suspend fun crearSecreto(@Body request: CrearSecretoRequest): Response<SecretoResponse>

    /**
     * Obtiene todos los secretos accesibles por el usuario actual.
     * Incluye secretos propios y compartidos con el usuario.
     */
    @GET("/api/secretos")
    suspend fun getSecretos(): Response<List<SecretoResponse>>

    /**
     * Obtiene un secreto específico por ID.
     * Solo si el usuario es autor o tiene acceso compartido.
     */
    @GET("/api/secretos/{id}")
    suspend fun getSecretoById(@Path("id") secretoId: Long): Response<SecretoResponse>

    /**
     * Comparte un secreto con otro usuario.
     * Re-cifra la clave AES con la clave pública del destinatario.
     */
    @POST("/api/secretos/{id}/compartir")
    suspend fun compartirSecreto(
        @Path("id") secretoId: Long,
        @Body request: CompartirSecretoRequest
    ): Response<Void>

    /**
     * Revoca el acceso de un usuario a un secreto.
     * Solo el autor puede revocar.
     */
    @DELETE("/api/secretos/{secretoId}/compartir/{usuarioId}")
    suspend fun revocarAcceso(
        @Path("secretoId") secretoId: Long,
        @Path("usuarioId") usuarioId: Long
    ): Response<Void>

    /**
     * Elimina un secreto permanentemente.
     * Solo el autor puede eliminar.
     */
    @DELETE("/api/secretos/{id}")
    suspend fun eliminarSecreto(@Path("id") secretoId: Long): Response<Void>

    /**
     * Obtiene la clave pública del servidor.
     * Necesaria para verificar certificados de usuarios.
     */
    @GET("/api/crypto/public-key")
    suspend fun getPublicKeyServidor(): Response<ClavePublicaServidorResponse>
}

