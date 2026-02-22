package com.example.navigationcompose.domain.model

data class Ejercicio(
    val id: Long,
    val nombre: String,
    val tipo: String,
    val imageUrl: String,
    val descripcion: String
)

data class Entrenamiento(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val ejercicios: List<Ejercicio>
)

/**
 * Modelo unificado de Usuario para toda la aplicación.
 * Incluye campos opcionales para criptografía (secretos).
 */
data class Usuario(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val rol: String,
    // Campos de criptografía (opcionales - solo para secretos)
    val publicKey: ByteArray? = null,
    val certificado: ByteArray? = null,
    val certificadoVerificado: Boolean = false
) {
    // Override equals y hashCode para ignorar ByteArray en comparaciones
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Usuario

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}

