package com.example.navigationcompose.data.repository

import android.util.Base64
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.remote.api.SecretosApiService
import com.example.navigationcompose.data.remote.entity.*
import com.example.navigationcompose.data.security.CryptoManager
import com.example.navigationcompose.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.PublicKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestionar secretos cifrados end-to-end.
 *
 * Responsabilidades:
 * - Cifrar contenido antes de enviarlo al servidor
 * - Descifrar contenido recibido del servidor
 * - Firmar datos con clave privada
 * - Verificar firmas de otros usuarios
 * - Gestionar compartición de secretos (re-cifrado de claves)
 */
@Singleton
class SecretosRepository @Inject constructor(
    private val apiService: SecretosApiService,
    private val cryptoManager: CryptoManager
) {

    // Cache de clave pública del servidor (para verificar certificados)
    private var publicKeyServidor: PublicKey? = null

    // ==================== LISTAR USUARIOS ====================

    /**
     * Obtiene la lista de usuarios con los que se pueden compartir secretos.
     * Incluye verificación de certificados.
     */
    suspend fun getUsuarios(): NetworkResult<List<Usuario>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUsuarios()
            if (response.isSuccessful && response.body() != null) {
                val usuarios = response.body()!!.map { it.toDomainUsuario() }

                // Verificar certificados de todos los usuarios
                val usuariosVerificados = usuarios.map { usuario ->
                    val certificadoValido = verificarCertificado(
                        usuario.publicKey ?: byteArrayOf(),
                        usuario.certificado ?: byteArrayOf()
                    )
                    usuario.copy(certificadoVerificado = certificadoValido)
                }

                NetworkResult.Success(usuariosVerificados)
            } else {
                NetworkResult.Error("${Constantes.ERROR_OBTENER_USUARIOS}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_RED_GENERICO}${e.message}")
        }
    }

    /**
     * Obtiene un usuario específico por ID.
     */
    suspend fun getUsuarioById(usuarioId: Long): NetworkResult<Usuario> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUsuarioById(usuarioId)
            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!.toDomainUsuario()
                val certificadoValido = verificarCertificado(
                    usuario.publicKey ?: byteArrayOf(),
                    usuario.certificado ?: byteArrayOf()
                )
                NetworkResult.Success(usuario.copy(certificadoVerificado = certificadoValido))
            } else {
                NetworkResult.Error(Constantes.ERROR_USUARIO_NO_ENCONTRADO)
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    // ==================== CREAR SECRETO ====================

    /**
     * Crea un secreto cifrado end-to-end.
     *
     * Proceso:
     * 1. Generar clave AES aleatoria
     * 2. Cifrar contenido con AES-GCM
     * 3. Obtener clave privada del usuario (con password)
     * 4. Firmar contenido cifrado
     * 5. Obtener clave pública del receptor
     * 6. Cifrar clave AES con clave pública del receptor
     * 7. Enviar todo al servidor
     */
    suspend fun crearSecreto(
        contenidoPlano: String,
        receptorId: Long,
        passwordUsuario: String
    ): NetworkResult<Secreto> = withContext(Dispatchers.IO) {
        try {
            // 1. Generar clave AES y IV
            val aesKey = cryptoManager.generateAESKey()
            val iv = cryptoManager.generateIV()

            // 2. Cifrar contenido
            val contenidoBytes = contenidoPlano.toByteArray(Charsets.UTF_8)
            val contenidoCifrado = cryptoManager.encryptAES_GCM(contenidoBytes, aesKey, iv)

            // 3. Cargar clave privada del autor
            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_PASSWORD_INCORRECTA)
            }

            // 4. Firmar contenido cifrado
            val firma = cryptoManager.signData(contenidoCifrado, privateKey)

            // 5. Obtener clave pública del receptor
            val receptorResponse = apiService.getUsuarioById(receptorId)
            if (!receptorResponse.isSuccessful || receptorResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_RECEPTOR_NO_ENCONTRADO)
            }

            val receptor = receptorResponse.body()!!
            val publicKeyReceptorBytes = Base64.decode(receptor.publicKey, Base64.DEFAULT)
            val publicKeyReceptor = cryptoManager.bytesToPublicKey(publicKeyReceptorBytes)

            // 6. Verificar certificado del receptor
            val certificadoReceptorBytes = Base64.decode(receptor.certificado, Base64.DEFAULT)
            if (!verificarCertificado(publicKeyReceptorBytes, certificadoReceptorBytes)) {
                return@withContext NetworkResult.Error(Constantes.ERROR_CERTIFICADO_INVALIDO)
            }

            // 7. Cifrar clave AES con clave pública del receptor
            val aesKeyBytes = aesKey.encoded
            val claveAESCifrada = cryptoManager.encryptRSA(aesKeyBytes, publicKeyReceptor)

            // 8. Preparar request
            val request = CrearSecretoRequest(
                receptorId = receptorId,
                contenidoCifrado = Base64.encodeToString(contenidoCifrado, Base64.NO_WRAP),
                claveAESCifrada = Base64.encodeToString(claveAESCifrada, Base64.NO_WRAP),
                firma = Base64.encodeToString(firma, Base64.NO_WRAP),
                iv = Base64.encodeToString(iv, Base64.NO_WRAP)
            )

            // 9. Enviar al servidor
            val response = apiService.crearSecreto(request)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.toDomain())
            } else {
                NetworkResult.Error("${Constantes.ERROR_CREAR_SECRETO}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    // ==================== LISTAR SECRETOS ====================

    /**
     * Obtiene todos los secretos accesibles por el usuario.
     */
    suspend fun getSecretos(): NetworkResult<List<Secreto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSecretos()
            if (response.isSuccessful && response.body() != null) {
                val secretos = response.body()!!.map { it.toDomain() }
                NetworkResult.Success(secretos)
            } else {
                NetworkResult.Error("${Constantes.ERROR_OBTENER_SECRETOS}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    // ==================== VER SECRETO (DESCIFRAR) ====================

    /**
     * Descifra y verifica un secreto.
     *
     * Proceso:
     * 1. Obtener secreto del servidor
     * 2. Cargar clave privada del usuario
     * 3. Descifrar clave AES
     * 4. Descifrar contenido
     * 5. Verificar firma del autor
     */
    suspend fun descifrarSecreto(
        secretoId: Long,
        passwordUsuario: String
    ): NetworkResult<SecretoDescifrado> = withContext(Dispatchers.IO) {
        try {
            // 1. Obtener secreto
            val response = apiService.getSecretoById(secretoId)
            if (!response.isSuccessful || response.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_SECRETO_NO_ENCONTRADO)
            }

            val secretoEntity = response.body()!!

            // 2. Cargar clave privada del usuario
            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_PASSWORD_INCORRECTA)
            }

            // 3. Descifrar clave AES
            val claveAESCifradaBytes = Base64.decode(secretoEntity.claveAESCifrada, Base64.DEFAULT)
            val aesKeyBytes = cryptoManager.decryptRSA(claveAESCifradaBytes, privateKey)
            val aesKey = cryptoManager.bytesToAESKey(aesKeyBytes)

            // 4. Descifrar contenido
            val ivBytes = Base64.decode(secretoEntity.iv, Base64.DEFAULT)
            val contenidoCifradoBytes = Base64.decode(secretoEntity.contenidoCifrado, Base64.DEFAULT)
            val contenidoBytes = cryptoManager.decryptAES_GCM(contenidoCifradoBytes, aesKey, ivBytes)
            val contenidoPlano = String(contenidoBytes, Charsets.UTF_8)

            // 5. Verificar firma del autor
            val publicKeyAutorBytes = Base64.decode(secretoEntity.autor.publicKey, Base64.DEFAULT)
            val publicKeyAutor = cryptoManager.bytesToPublicKey(publicKeyAutorBytes)
            val firmaBytes = Base64.decode(secretoEntity.firma, Base64.DEFAULT)
            val firmaValida = cryptoManager.verifySignature(contenidoCifradoBytes, firmaBytes, publicKeyAutor)

            // 6. Verificar certificado del autor
            val certificadoAutorBytes = Base64.decode(secretoEntity.autor.certificado, Base64.DEFAULT)
            val certificadoValido = verificarCertificado(publicKeyAutorBytes, certificadoAutorBytes)

            if (!certificadoValido) {
                return@withContext NetworkResult.Error(Constantes.ERROR_CERTIFICADO_AUTOR_INVALIDO)
            }

            val secreto = secretoEntity.toDomain()
            val secretoDescifrado = SecretoDescifrado(
                secreto = secreto,
                contenidoPlano = contenidoPlano,
                firmaValida = firmaValida
            )

            NetworkResult.Success(secretoDescifrado)
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_DESCIFRAR_SECRETO}${e.message}")
        }
    }

    // ==================== COMPARTIR SECRETO ====================

    /**
     * Comparte un secreto con otro usuario.
     * Re-cifra la clave AES con la clave pública del destinatario.
     */
    suspend fun compartirSecreto(
        secretoId: Long,
        receptorId: Long,
        passwordUsuario: String
    ): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Obtener el secreto para obtener la clave AES
            val secretoResponse = apiService.getSecretoById(secretoId)
            if (!secretoResponse.isSuccessful || secretoResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_SECRETO_NO_ENCONTRADO)
            }

            val secreto = secretoResponse.body()!!

            // 2. Cargar clave privada del usuario actual
            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_PASSWORD_INCORRECTA)
            }

            // 3. Descifrar clave AES con nuestra clave privada
            val claveAESCifradaBytes = Base64.decode(secreto.claveAESCifrada, Base64.DEFAULT)
            val aesKeyBytes = cryptoManager.decryptRSA(claveAESCifradaBytes, privateKey)

            // 4. Obtener clave pública del nuevo destinatario
            val receptorResponse = apiService.getUsuarioById(receptorId)
            if (!receptorResponse.isSuccessful || receptorResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_RECEPTOR_NO_ENCONTRADO)
            }

            val receptor = receptorResponse.body()!!
            val publicKeyReceptorBytes = Base64.decode(receptor.publicKey, Base64.DEFAULT)
            val publicKeyReceptor = cryptoManager.bytesToPublicKey(publicKeyReceptorBytes)

            // 5. Verificar certificado del receptor
            val certificadoBytes = Base64.decode(receptor.certificado, Base64.DEFAULT)
            if (!verificarCertificado(publicKeyReceptorBytes, certificadoBytes)) {
                return@withContext NetworkResult.Error(Constantes.ERROR_CERTIFICADO_INVALIDO)
            }

            // 6. RE-CIFRAR clave AES con clave pública del nuevo destinatario
            val claveAESReCifrada = cryptoManager.encryptRSA(aesKeyBytes, publicKeyReceptor)

            // 7. Enviar al servidor
            val request = CompartirSecretoRequest(
                receptorId = receptorId,
                claveAESCifradaDestinatario = Base64.encodeToString(claveAESReCifrada, Base64.NO_WRAP)
            )

            val response = apiService.compartirSecreto(secretoId, request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("${Constantes.ERROR_COMPARTIR_SECRETO}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    // ==================== REVOCAR ACCESO ====================

    /**
     * Revoca el acceso de un usuario a un secreto.
     */
    suspend fun revocarAcceso(secretoId: Long, usuarioId: Long): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.revocarAcceso(secretoId, usuarioId)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("${Constantes.ERROR_REVOCAR_ACCESO}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    // ==================== ELIMINAR SECRETO ====================

    /**
     * Elimina un secreto permanentemente.
     */
    suspend fun eliminarSecreto(secretoId: Long): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.eliminarSecreto(secretoId)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("${Constantes.ERROR_ELIMINAR_SECRETO}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    // ==================== VERIFICACIÓN DE CERTIFICADOS ====================

    /**
     * Verifica que un certificado (firma del servidor) sea válido para una clave pública.
     *
     * Proceso:
     * 1. Obtener clave pública del servidor (si no está en cache)
     * 2. Verificar firma del servidor sobre la clave pública del usuario
     */
    private suspend fun verificarCertificado(publicKeyBytes: ByteArray, certificadoBytes: ByteArray): Boolean {
        return try {
            // Obtener clave pública del servidor si no está en cache
            if (publicKeyServidor == null) {
                val response = apiService.getPublicKeyServidor()
                if (response.isSuccessful && response.body() != null) {
                    val publicKeyServidorBytes = Base64.decode(response.body()!!.publicKey, Base64.DEFAULT)
                    publicKeyServidor = cryptoManager.bytesToPublicKey(publicKeyServidorBytes)
                } else {
                    return false
                }
            }

            // Verificar firma del servidor
            cryptoManager.verifySignature(publicKeyBytes, certificadoBytes, publicKeyServidor!!)
        } catch (_: Exception) {
            false
        }
    }

    // ==================== CONVERSIONES ====================

    /**
     * Convierte UsuarioSecretoDto a Usuario del dominio.
     */
    private fun UsuarioSecretoDto.toDomainUsuario() = Usuario(
        id = id,
        username = username,
        email = "", // No disponible en UsuarioSecretoDto
        nombre = nombre,
        rol = Constantes.USER,
        publicKey = Base64.decode(publicKey, Base64.DEFAULT),
        certificado = Base64.decode(certificado, Base64.DEFAULT),
        certificadoVerificado = false
    )

    /**
     * Convierte SecretoResponse a Secreto del dominio.
     */
    private fun SecretoResponse.toDomain() = Secreto(
        id = id,
        autor = autor.toDomainUsuario(),
        contenidoDescifrado = null,
        fechaCreacion = fechaCreacion,
        compartidoCon = compartidos.map { it.toDomainUsuario() },
        esAutor = false, // Se debe determinar comparando con usuario actual
        firmaValida = null
    )
}

