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

@Singleton
class SecretosRepository @Inject constructor(
    private val apiService: SecretosApiService,
    private val cryptoManager: CryptoManager
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

            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_PASSWORD_INCORRECTA)
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
                    fechaCreacion = "",
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
                        fechaCreacion = "",
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
        passwordUsuario: String
    ): NetworkResult<SecretoDescifrado> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSecretoById(secretoId)
            if (!response.isSuccessful || response.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_SECRETO_NO_ENCONTRADO)
            }

            val secretoEntity = response.body()!!

            // 1. Obtener datos del autor (clave pública + certificado)
            val autorResponse = apiService.getUsuarioById(secretoEntity.autorId)
            if (!autorResponse.isSuccessful || autorResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_USUARIO_NO_ENCONTRADO)
            }
            val autorDto = autorResponse.body()!!

            // 2. Decodificar y verificar que existen datos
            val publicKeyAutorBytes = try {
                if (autorDto.publicKey.isNullOrBlank()) {
                    return@withContext NetworkResult.Error("El autor no tiene clave pública configurada")
                }
                Base64.decode(autorDto.publicKey, Base64.DEFAULT)
            } catch (e: Exception) {
                return@withContext NetworkResult.Error("Error decodificando clave pública del autor: ${e.message}")
            }

            val certificadoAutorBytes = try {
                if (autorDto.certificado.isNullOrBlank()) {
                    android.util.Log.w("SecretosRepo", "⚠️ Certificado del autor está vacío")
                    byteArrayOf()
                } else {
                    Base64.decode(autorDto.certificado, Base64.DEFAULT)
                }
            } catch (e: Exception) {
                android.util.Log.e("SecretosRepo", "❌ Error decodificando certificado: ${e.message}")
                byteArrayOf()
            }

            val publicKeyAutor = try {
                cryptoManager.bytesToPublicKey(publicKeyAutorBytes)
            } catch (e: Exception) {
                android.util.Log.e("SecretosRepo", "❌ Error convirtiendo clave pública: ${e.message}")
                return@withContext NetworkResult.Error("Clave pública del autor inválida")
            }

            android.util.Log.d("SecretosRepo", "📝 Verificando certificado del autor '${autorDto.username}'")
            android.util.Log.d("SecretosRepo", "   - Longitud clave pública: ${publicKeyAutorBytes.size} bytes")
            android.util.Log.d("SecretosRepo", "   - Longitud certificado: ${certificadoAutorBytes.size} bytes")

            // 3. Verificar certificado del autor con el servidor CA (solo si existe)
            val certificadoValido = if (certificadoAutorBytes.isNotEmpty()) {
                val resultado = verificarCertificado(publicKeyAutorBytes, certificadoAutorBytes)
                android.util.Log.d("SecretosRepo", "   - Resultado verificación: $resultado")
                resultado
            } else {
                android.util.Log.w("SecretosRepo", "   - Sin certificado para verificar")
                false
            }

            if (!certificadoValido) {
                android.util.Log.e("SecretosRepo", "❌ Certificado del autor no válido")
                return@withContext NetworkResult.Error("Certificado del autor no válido")
            }

            // 4. Verificar firma del contenido del secreto
            val contenidoCifradoBytes = Base64.decode(secretoEntity.contenidoCifrado, Base64.DEFAULT)
            val firmaBytes = Base64.decode(secretoEntity.firma, Base64.DEFAULT)
            val firmaValida = try {
                cryptoManager.verifySignature(contenidoCifradoBytes, firmaBytes, publicKeyAutor)
            } catch (_: Exception) {
                false
            }

            if (!firmaValida) {
                return@withContext NetworkResult.Error("La firma del secreto no es válida")
            }

            // 5. Descifrar clave privada del usuario con su contraseña
            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_PASSWORD_INCORRECTA)
            }

            // 6. Descifrar clave AES del secreto usando la clave privada del usuario
            val claveAESCifradaBytes = Base64.decode(secretoEntity.claveAESCifrada, Base64.DEFAULT)
            val aesKeyBytes = try {
                cryptoManager.decryptRSA(claveAESCifradaBytes, privateKey)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error("Error descifrando clave AES")
            }
            val aesKey = cryptoManager.bytesToAESKey(aesKeyBytes)

            // 7. Descifrar contenido del secreto usando la clave AES
            val ivBytes = Base64.decode(secretoEntity.iv, Base64.DEFAULT)
            val contenidoBytes = try {
                cryptoManager.decryptAES_GCM(contenidoCifradoBytes, aesKey, ivBytes)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error("Error descifrando contenido")
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
                fechaCreacion = "",
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

            val privateKey = try {
                cryptoManager.loadAndDecryptPrivateKey(passwordUsuario)
            } catch (_: Exception) {
                return@withContext NetworkResult.Error(Constantes.ERROR_PASSWORD_INCORRECTA)
            }

            val claveAESCifradaBytes = Base64.decode(secreto.claveAESCifrada, Base64.DEFAULT)
            val aesKeyBytes = cryptoManager.decryptRSA(claveAESCifradaBytes, privateKey)

            val receptorResponse = apiService.getUsuarioById(receptorId)
            if (!receptorResponse.isSuccessful || receptorResponse.body() == null) {
                return@withContext NetworkResult.Error(Constantes.ERROR_RECEPTOR_NO_ENCONTRADO)
            }

            val receptor = receptorResponse.body()!!
            val publicKeyReceptorBytes = Base64.decode(receptor.publicKey, Base64.DEFAULT)
            val publicKeyReceptor = cryptoManager.bytesToPublicKey(publicKeyReceptorBytes)

            val certificadoBytes = Base64.decode(receptor.certificado, Base64.DEFAULT)
            if (!verificarCertificado(publicKeyReceptorBytes, certificadoBytes)) {
                return@withContext NetworkResult.Error(Constantes.ERROR_CERTIFICADO_INVALIDO)
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
                android.util.Log.w("SecretosRepo", "Certificado o clave pública vacíos")
                return false
            }

            if (publicKeyServidor == null) {
                val response = apiService.getPublicKeyServidor()
                if (response.isSuccessful && response.body() != null) {
                    val publicKeyServidorBytes = Base64.decode(response.body()!!.publicKey, Base64.DEFAULT)
                    publicKeyServidor = cryptoManager.bytesToPublicKey(publicKeyServidorBytes)
                    android.util.Log.d("SecretosRepo", "✅ Clave pública del servidor CA cargada correctamente")
                } else {
                    android.util.Log.e("SecretosRepo", "❌ Error obteniendo clave pública del servidor: ${response.code()}")
                    return false
                }
            }

            val esValido = cryptoManager.verifySignature(publicKeyBytes, certificadoBytes, publicKeyServidor!!)

            if (esValido) {
                android.util.Log.d("SecretosRepo", "✅ Certificado verificado correctamente")
            } else {
                android.util.Log.w("SecretosRepo", "⚠️ Certificado NO válido")
            }

            esValido
        } catch (e: Exception) {
            android.util.Log.e("SecretosRepo", "❌ Error verificando certificado: ${e.message}", e)
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
