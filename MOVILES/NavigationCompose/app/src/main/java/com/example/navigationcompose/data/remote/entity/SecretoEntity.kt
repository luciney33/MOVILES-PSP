package com.example.navigationcompose.data.remote.entity

import com.example.navigationcompose.common.Constantes
import com.google.gson.annotations.SerializedName

/**
 * DTOs para el sistema de secretos cifrados end-to-end
 */

// ==================== REQUEST ====================

/**
 * Request para crear un secreto cifrado.
 *
 * Todos los campos en Base64 excepto receptorId.
 */
data class CrearSecretoRequest(
    @SerializedName("receptorId")
    val receptorId: Long,

    @SerializedName("contenidoCifrado")
    val contenidoCifrado: String, // Base64 del contenido cifrado con AES-GCM

    @SerializedName("claveAESCifrada")
    val claveAESCifrada: String, // Base64 de la clave AES cifrada con RSA del receptor

    @SerializedName("firma")
    val firma: String, // Base64 de la firma digital del autor

    @SerializedName("iv")
    val iv: String // Base64 del IV usado para AES-GCM
)

/**
 * Request para compartir un secreto con otro usuario.
 */
data class CompartirSecretoRequest(
    @SerializedName("receptorId")
    val receptorId: Long,

    @SerializedName("claveAESCifradaDestinatario")
    val claveAESCifradaDestinatario: String // Base64 de la clave AES re-cifrada con RSA del nuevo destinatario
)

// ==================== RESPONSE ====================

/**
 * Representa un secreto cifrado en el servidor.
 * El servidor NUNCA tiene acceso al contenido descifrado.
 */
data class SecretoResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("autor")
    val autor: UsuarioSecretoDto,

    @SerializedName("contenidoCifrado")
    val contenidoCifrado: String, // Base64

    @SerializedName("claveAESCifrada")
    val claveAESCifrada: String, // Base64 - cifrada con RSA del usuario actual

    @SerializedName("firma")
    val firma: String, // Base64 - firma del autor

    @SerializedName("iv")
    val iv: String, // Base64

    @SerializedName("fechaCreacion")
    val fechaCreacion: String,

    @SerializedName("compartidos")
    val compartidos: List<UsuarioSecretoDto> = emptyList()
)

/**
 * DTO simplificado de usuario para secretos.
 */
data class UsuarioSecretoDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("publicKey")
    val publicKey: String, // Base64

    @SerializedName("certificado")
    val certificado: String // Base64 - firma del servidor sobre la publicKey
)

/**
 * Lista de secretos accesibles por el usuario.
 */
data class ListaSecretosResponse(
    @SerializedName("secretos")
    val secretos: List<SecretoResponse>
)

/**
 * Response al obtener la clave pública del servidor.
 * Necesaria para verificar certificados de usuarios.
 */
data class ClavePublicaServidorResponse(
    @SerializedName("publicKey")
    val publicKey: String // Base64 - clave pública RSA del servidor
)

// ==================== MAPPERS/CONVERSIONES ====================

/**
 * Convierte UsuarioEntity (de GymApiModels) a UsuarioSecretoDto.
 * Reutiliza el modelo existente evitando duplicación de código.
 */
fun UsuarioEntity.toUsuarioSecretoDto() = UsuarioSecretoDto(
    id = id,
    username = username,
    nombre = nombre,
    publicKey = publicKey ?: "",
    certificado = certificado ?: ""
)

/**
 * Convierte UsuarioSecretoDto a UsuarioEntity.
 * Útil cuando se recibe información de secretos y se quiere usar el modelo unificado.
 */
fun UsuarioSecretoDto.toUsuarioEntity() = UsuarioEntity(
    id = id,
    username = username,
    email = "", // No disponible en UsuarioSecretoDto
    nombre = nombre,
    password = "", // No disponible en UsuarioSecretoDto (nunca se envía)
    rol = Constantes.USER, // Asumimos USER por defecto
    activo = true,
    publicKey = publicKey,
    certificado = certificado
)
