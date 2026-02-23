package com.example.navigationcompose.data.repository

import android.util.Base64
import android.util.Log
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.common.NetworkResult
import com.example.navigationcompose.data.remote.api.SecretosApiService
import com.example.navigationcompose.data.remote.entity.*
import com.example.navigationcompose.data.security.CryptoManager
import com.example.navigationcompose.data.security.SessionManager
import com.example.navigationcompose.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.PublicKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecretosRepository @Inject constructor(
    private val apiService: SecretosApiService,
    private val cryptoManager: CryptoManager,
    private val sessionManager: SessionManager
) {

    private var publicKeyServidor: PublicKey? = null

    suspend fun getUsuarios(): NetworkResult<List<Usuario>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUsuarios()
            if (response.isSuccessful && response.body() != null) {
                val usuarios = response.body()!!.map { it.toDomainUsuario() }

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

    suspend fun crearSecreto(
        contenidoPlano: String,
        passwordUsuario: String
    ): NetworkResult<Secreto> = withContext(Dispatchers.IO) {
        try {
            val aesKey = cryptoManager.generateAESKey()
            val iv = cryptoManager.generateIV()

            val contenidoBytes = contenidoPlano.toByteArray(Charsets.UTF_8)
            val contenidoCifrado = cryptoManager.encryptAES_GCM(contenidoBytes, aesKey, iv)

            val currentUser = sessionManager.getCurrentUser()
            val username = currentUser?.username ?: run {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.ERROR_NO_USER_SESSION_CREATE_SECRET)
                return@withContext NetworkResult.Error(Constantes.ERROR_USER_NOT_AUTHENTICATED)
            }

            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario, username)
            } catch (e: Exception) {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.ERROR_DECRYPT_PRIVATE_KEY_LOCAL}${e.message}")
                return@withContext NetworkResult.Error("${Constantes.ERROR_PASSWORD_INCORRECTA}${Constantes.ERROR_PASSWORD_INCORRECTA_GENERA_CLAVES}")
            }

            val firma = cryptoManager.signData(contenidoCifrado, privateKey)

            val publicKeyUsuario = cryptoManager.getPublicKey()

            val aesKeyBytes = aesKey.encoded
            val claveAESCifrada = cryptoManager.encryptRSA(aesKeyBytes, publicKeyUsuario)

            val request = CrearSecretoRequest(
                contenidoCifrado = Base64.encodeToString(contenidoCifrado, Base64.NO_WRAP),
                claveAESCifrada = Base64.encodeToString(claveAESCifrada, Base64.NO_WRAP),
                firma = Base64.encodeToString(firma, Base64.NO_WRAP),
                iv = Base64.encodeToString(iv, Base64.NO_WRAP)
            )

            val response = apiService.crearSecreto(request)
            if (response.isSuccessful && response.body() != null) {
                val secretoId = response.body()!!
                val secreto = Secreto(
                    id = secretoId,
                    autor = Usuario(
                        id = 0,
                        username = "",
                        email = "",
                        nombre = "",
                        rol = Constantes.USER
                    ),
                    contenidoDescifrado = null,
                    compartidoCon = emptyList(),
                    esAutor = true,
                    firmaValida = null
                )
                NetworkResult.Success(secreto)
            } else {
                NetworkResult.Error("${Constantes.ERROR_CREAR_SECRETO}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    suspend fun getSecretos(): NetworkResult<List<Secreto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.listarSecretos()
            if (response.isSuccessful && response.body() != null) {
                val secretos = response.body()!!.map { dto ->
                    Secreto(
                        id = dto.id,
                        autor = Usuario(
                            id = dto.autorId,
                            username = dto.autorUsername,
                            email = "",
                            nombre = dto.autorNombre,
                            rol = Constantes.USER
                        ),
                        contenidoDescifrado = null,
                        compartidoCon = emptyList(),
                        esAutor = dto.esAutor,
                        firmaValida = null
                    )
                }
                NetworkResult.Success(secretos)
            } else {
                NetworkResult.Error("${Constantes.ERROR_OBTENER_SECRETOS}${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error("${Constantes.ERROR_GENERICO_MENSAJE}${e.message}")
        }
    }

    suspend fun descifrarSecreto(
        secretoId: Long,
        passwordUsuario: String,
        autorId: Long? = null
    ): NetworkResult<SecretoDescifrado> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSecretoById(secretoId)
            if (!response.isSuccessful || response.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_SECRETO_NO_ENCONTRADO)
            }

            val secretoEntity = response.body()!!
            val autorResponse = apiService.getUsuarioById(secretoEntity.autorId)
            if (!autorResponse.isSuccessful || autorResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_USUARIO_NO_ENCONTRADO)
            }
            val autorDto = autorResponse.body()!!
            val publicKeyAutorBytes = try {
                if (autorDto.publicKey.isNullOrBlank()) {
                    return@withContext NetworkResult.Error(Constantes.ERROR_AUTOR_NO_CLAVE_PUBLICA)
                }
                Base64.decode(autorDto.publicKey, Base64.DEFAULT)
            } catch (e: Exception) {
                return@withContext NetworkResult.Error("${Constantes.ERROR_DECODIFICANDO_CLAVE_PUBLICA}${e.message}")
            }

            val certificadoAutorBytes = try {
                if (autorDto.certificado.isNullOrBlank()) {
                    Log.w(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.WARN_EMPTY_CERTIFICATE)
                    byteArrayOf()
                } else {
                    Base64.decode(autorDto.certificado, Base64.DEFAULT)
                }
            } catch (e: Exception) {
                Log.e(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.ERROR_DECODIFICANDO_CERTIFICADO}${e.message}")
                byteArrayOf()
            }

            val publicKeyAutor = try {
                cryptoManager.bytesToPublicKey(publicKeyAutorBytes)
            } catch (e: Exception) {
                Log.e(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.ERROR_CLAVE_PUBLICA_INVALIDA)
                return@withContext NetworkResult.Error(Constantes.ERROR_CLAVE_PUBLICA_INVALIDA)
            }

            Log.d(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.INFO_VERIFYING_CERTIFICATE}${autorDto.username}'")
            Log.d(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.INFO_PUBLIC_KEY_LENGTH}${publicKeyAutorBytes.size} bytes")
            Log.d(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.INFO_CERTIFICATE_LENGTH}${certificadoAutorBytes.size} bytes")

            val certificadoValido = if (certificadoAutorBytes.isNotEmpty()) {
                val resultado = verificarCertificado(publicKeyAutorBytes, certificadoAutorBytes)
                Log.d(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.INFO_VERIFICATION_RESULT}$resultado")
                resultado
            } else {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.WARN_NO_CERTIFICATE_TO_VERIFY)
                false
            }

            if (!certificadoValido) {
                Log.e(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.ERROR_INVALID_AUTHOR_CERTIFICATE)
                return@withContext NetworkResult.Error(Constantes.ERROR_INVALID_AUTHOR_CERTIFICATE)
            }

            val contenidoCifradoBytes = Base64.decode(secretoEntity.contenidoCifrado, Base64.DEFAULT)
            val firmaBytes = Base64.decode(secretoEntity.firma, Base64.DEFAULT)
            val firmaValida = try {
                cryptoManager.verifySignature(contenidoCifradoBytes, firmaBytes, publicKeyAutor)
            } catch (_: Exception) {
                false
            }

            if (!firmaValida) {
                return@withContext NetworkResult.Error(Constantes.ERROR_INVALID_SIGNATURE)
            }

            val currentUser = sessionManager.getCurrentUser()
            val username = currentUser?.username ?: run {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.ERROR_NO_USER_SESSION_DECRYPT_SECRET)
                return@withContext NetworkResult.Error(Constantes.ERROR_USER_NOT_AUTHENTICATED)
            }

            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario, username)
            } catch (e: Exception) {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.ERROR_LOAD_DECRYPT_PRIVATE_KEY_LOCAL}${e.message}")
                return@withContext NetworkResult.Error("${Constantes.ERROR_PASSWORD_INCORRECTA}${Constantes.ERROR_PASSWORD_INCORRECTA_TIENES_CLAVES}")
            }

            val claveAESCifradaBytes = Base64.decode(secretoEntity.claveAESCifrada, Base64.DEFAULT)
            val aesKeyBytes = try {
                cryptoManager.decryptRSA(claveAESCifradaBytes, privateKey)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_DECRYPTING_AES_KEY)
            }
            val aesKey = cryptoManager.bytesToAESKey(aesKeyBytes)

            val ivBytes = Base64.decode(secretoEntity.iv, Base64.DEFAULT)
            val contenidoBytes = try {
                cryptoManager.decryptAES_GCM(contenidoCifradoBytes, aesKey, ivBytes)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_DECRYPTING_CONTENT)
            }
            val contenidoPlano = String(contenidoBytes, Charsets.UTF_8)

            val autor = Usuario(
                id = secretoEntity.autorId,
                username = secretoEntity.autorUsername,
                email = "",
                nombre = secretoEntity.autorNombre,
                rol = Constantes.USER,
                publicKey = publicKeyAutorBytes,
                certificado = certificadoAutorBytes,
                certificadoVerificado = certificadoValido
            )

            val secreto = Secreto(
                id = secretoEntity.id,
                autor = autor,
                contenidoDescifrado = null,
                compartidoCon = emptyList(),
                esAutor = !secretoEntity.esCompartido,
                firmaValida = firmaValida
            )

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

    suspend fun compartirSecreto(
        secretoId: Long,
        receptorId: Long,
        passwordUsuario: String
    ): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val secretoResponse = apiService.getSecretoById(secretoId)
            if (!secretoResponse.isSuccessful || secretoResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_SECRETO_NO_ENCONTRADO)
            }

            val secreto = secretoResponse.body()!!

            val currentUser = sessionManager.getCurrentUser()
            val username = currentUser?.username ?: run {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.ERROR_NO_USER_SESSION_SHARE_SECRET)
                return@withContext NetworkResult.Error(Constantes.ERROR_USER_NOT_AUTHENTICATED)
            }

            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario, username)
            } catch (e: Exception) {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, "${Constantes.ERROR_LOAD_DECRYPT_PRIVATE_KEY_LOCAL}${e.message}")
                return@withContext NetworkResult.Error("${Constantes.ERROR_PASSWORD_INCORRECTA}${Constantes.ERROR_PASSWORD_INCORRECTA_TIENES_CLAVES}")
            }

            val claveAESCifradaBytes = Base64.decode(secreto.claveAESCifrada, Base64.DEFAULT)
            val aesKeyBytes = cryptoManager.decryptRSA(claveAESCifradaBytes, privateKey)

            val receptorResponse = apiService.getUsuarioById(receptorId)
            if (!receptorResponse.isSuccessful || receptorResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_RECEPTOR_NO_ENCONTRADO)
            }

            val receptor = receptorResponse.body()!!
            val publicKeyReceptorBytes = try {
                Base64.decode(receptor.publicKey, Base64.DEFAULT)
            } catch (e: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_RECEPTOR_NO_ENCONTRADO)
            }
            val publicKeyReceptor = cryptoManager.bytesToPublicKey(publicKeyReceptorBytes)

            if (receptor.certificado.isNullOrBlank()) {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.WARN_NO_CERTIFICATE_ON_SERVER_SHARE)
            } else {
                val certificadoBytes = Base64.decode(receptor.certificado, Base64.DEFAULT)
                val certOk = verificarCertificado(publicKeyReceptorBytes, certificadoBytes)
                if (!certOk) {
                    Log.e(Constantes.LOG_TAG_SECRETOS_REPO, Constantes.ERROR_INVALID_RECIPIENT_CERTIFICATE_ABORT_SHARE)
                    return@withContext NetworkResult.Error(Constantes.ERROR_CERTIFICADO_INVALIDO)
                }
            }

            val claveAESReCifrada = cryptoManager.encryptRSA(aesKeyBytes, publicKeyReceptor)

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

    private suspend fun verificarCertificado(publicKeyBytes: ByteArray, certificadoBytes: ByteArray): Boolean {
        return try {
            if (publicKeyBytes.isEmpty() || certificadoBytes.isEmpty()) {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, "Certificado o clave pública vacíos")
                return false
            }

            if (publicKeyServidor == null) {
                val response = apiService.getPublicKeyServidor()
                if (response.isSuccessful && response.body() != null) {
                    val publicKeyServidorBytes = Base64.decode(response.body()!!.publicKey, Base64.DEFAULT)
                    publicKeyServidor = cryptoManager.bytesToPublicKey(publicKeyServidorBytes)
                    Log.d(Constantes.LOG_TAG_SECRETOS_REPO, "Clave pública del servidor CA cargada correctamente")
                } else {
                    Log.e(Constantes.LOG_TAG_SECRETOS_REPO, "Error obteniendo clave pública del servidor: ${response.code()}")
                    return false
                }
            }

            var esValido = try {
                cryptoManager.verifySignature(publicKeyBytes, certificadoBytes, publicKeyServidor!!)
            } catch (e: Exception) {
                Log.e(Constantes.LOG_TAG_SECRETOS_REPO, "Error verificando firma con CA actual: ${e.message}")
                false
            }

            if (!esValido) {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, "Verificación fallida, reintentando recarga de la clave CA...")
                val response = apiService.getPublicKeyServidor()
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val publicKeyServidorBytes = Base64.decode(response.body()!!.publicKey, Base64.DEFAULT)
                        publicKeyServidor = cryptoManager.bytesToPublicKey(publicKeyServidorBytes)
                        esValido = cryptoManager.verifySignature(publicKeyBytes, certificadoBytes, publicKeyServidor!!)
                        Log.d(Constantes.LOG_TAG_SECRETOS_REPO, "Reintento verificación resultado: $esValido")
                    } catch (e: Exception) {
                        Log.e(Constantes.LOG_TAG_SECRETOS_REPO, "Reintento falló: ${e.message}")
                        esValido = false
                    }
                } else {
                    Log.e(Constantes.LOG_TAG_SECRETOS_REPO, "No se pudo recargar la clave CA para reintento: ${response.code()}")
                }
            }

            if (esValido) {
                Log.d(Constantes.LOG_TAG_SECRETOS_REPO, "Certificado verificado correctamente")
            } else {
                Log.w(Constantes.LOG_TAG_SECRETOS_REPO, "Certificado NO válido")
            }

            esValido
        } catch (e: Exception) {
            Log.e(Constantes.LOG_TAG_SECRETOS_REPO, "Error verificando certificado: ${e.message}", e)
            false
        }
    }

    private fun UsuarioSecretoDto.toDomainUsuario() = Usuario(
        id = id,
        username = username,
        email = "",
        nombre = nombre,
        rol = Constantes.USER,
        publicKey = Base64.decode(publicKey, Base64.DEFAULT),
        certificado = Base64.decode(certificado, Base64.DEFAULT),
        certificadoVerificado = false
    )
}
