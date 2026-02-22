# 📱 Guía Completa del Servidor para Cliente Kotlin/Android

## 📋 Índice

1. [Información General](#-información-general)
2. [Configuración Inicial](#-configuración-inicial)
3. [Autenticación y Usuarios](#-autenticación-y-usuarios)
4. [Gestión de Secretos](#-gestión-de-secretos)
5. [Criptografía](#-criptografía)
6. [Entrenamientos y Ejercicios](#-entrenamientos-y-ejercicios)
7. [Modelos de Datos (DTOs)](#-modelos-de-datos-dtos)
8. [Códigos de Estado HTTP](#-códigos-de-estado-http)
9. [Manejo de Errores](#-manejo-de-errores)

---

## 🌐 Información General

### URL Base del Servidor
```
http://localhost:8080
```

### Headers Comunes

```kotlin
// Para peticiones autenticadas (después del login)
val headers = mapOf(
    "Authorization" to "Bearer $accessToken",
    "Content-Type" to "application/json"
)

// Para peticiones sin autenticación (login, registro)
val headers = mapOf(
    "Content-Type" to "application/json"
)
```

### Arquitectura del Sistema

**Zero-Knowledge Server:**
- El servidor **NUNCA** almacena claves privadas
- El servidor **NUNCA** descifra secretos
- El servidor **SOLO** almacena datos cifrados
- El cliente es responsable de toda la criptografía

---

## ⚙️ Configuración Inicial

### 1. Configurar Retrofit

```kotlin
// API Service Interface
interface ApiService {
    
    // ============== AUTENTICACIÓN ==============
    
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UsuarioResponseDTO
    
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("/api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): ApiSuccessResponse
    
    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): LoginResponse
    
    // ============== USUARIOS ==============
    
    @GET("/api/usuarios")
    suspend fun getUsuariosPublicos(): List<UsuarioPublicoDTO>
    
    @GET("/api/usuarios/{id}")
    suspend fun getUsuarioPublico(@Path("id") id: Long): UsuarioPublicoDTO
    
    @PUT("/api/usuarios/public-key")
    suspend fun updatePublicKey(@Body request: UpdatePublicKeyRequest): UsuarioResponseDTO
    
    // ============== SECRETOS ==============
    
    @GET("/api/secretos")
    suspend fun listarSecretos(): List<SecretoSummaryDTO>
    
    @POST("/api/secretos")
    suspend fun crearSecreto(@Body request: CrearSecretoRequest): Long
    
    @GET("/api/secretos/{id}")
    suspend fun obtenerSecreto(@Path("id") id: Long): SecretoCifradoResponse
    
    @POST("/api/secretos/{id}/compartir")
    suspend fun compartirSecreto(
        @Path("id") id: Long,
        @Body request: CompartirSecretoRequest
    ): String
    
    @DELETE("/api/secretos/{id}/compartir/{receptorId}")
    suspend fun revocarAcceso(
        @Path("id") id: Long,
        @Path("receptorId") receptorId: Long
    ): Response<Void>
    
    @DELETE("/api/secretos/{id}")
    suspend fun borrarSecreto(@Path("id") id: Long): Response<Void>
    
    // ============== CRIPTOGRAFÍA ==============
    
    @GET("/api/crypto/public-key")
    suspend fun getServerPublicKey(): ClavePublicaServidorResponse
    
    // ============== ENTRENAMIENTOS ==============
    
    @GET("/api/entrenamientos")
    suspend fun listarEntrenamientos(): List<Entrenamiento>
    
    @GET("/api/entrenamientos/{id}")
    suspend fun obtenerEntrenamiento(@Path("id") id: Long): Entrenamiento
    
    @POST("/api/entrenamientos")
    suspend fun crearEntrenamiento(@Body entrenamiento: Entrenamiento): Entrenamiento
    
    @PUT("/api/entrenamientos/{id}")
    suspend fun actualizarEntrenamiento(
        @Path("id") id: Long,
        @Body entrenamiento: Entrenamiento
    ): Entrenamiento
    
    @DELETE("/api/entrenamientos/{id}")
    suspend fun borrarEntrenamiento(@Path("id") id: Long): Response<Void>
    
    // ============== EJERCICIOS ==============
    
    @GET("/api/ejercicios")
    suspend fun listarEjercicios(): List<Ejercicio>
}
```

### 2. Crear Cliente Retrofit

```kotlin
object RetrofitClient {
    private const val BASE_URL = "http://localhost:8080/"
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

---

## 🔐 Autenticación y Usuarios

### REGISTRO

#### Endpoint
```
POST /api/auth/register
```

#### Request
```kotlin
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val nombre: String,
    val rol: String = "USER",
    val publicKeyBase64: String? = null // Opcional, pero recomendado para secretos
)
```

#### Response (201 Created)
```kotlin
data class UsuarioResponseDTO(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val publicKey: String,      // Base64
    val certificado: String,     // Base64
    val rol: String
)
```

#### Ejemplo de Uso
```kotlin
suspend fun registrarUsuario(
    username: String,
    password: String,
    email: String,
    nombre: String,
    publicKey: ByteArray
) {
    try {
        val publicKeyBase64 = Base64.encodeToString(publicKey, Base64.NO_WRAP)
        
        val request = RegisterRequest(
            username = username,
            password = password,
            email = email,
            nombre = nombre,
            rol = "USER",
            publicKeyBase64 = publicKeyBase64
        )
        
        val response = apiService.register(request)
        
        // Guardar certificado en DataStore
        dataStore.edit { prefs ->
            prefs[CERTIFICADO_KEY] = response.certificado
            prefs[USER_ID_KEY] = response.id
        }
        
        Log.d("Auth", "Registro exitoso: ${response.username}")
    } catch (e: Exception) {
        Log.e("Auth", "Error en registro: ${e.message}")
        throw e
    }
}
```

---

### LOGIN

#### Endpoint
```
POST /api/auth/login
```

#### Request
```kotlin
data class LoginRequest(
    val username: String,
    val password: String
)
```

#### Response (200 OK)
```kotlin
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val requires2FA: Boolean?,
    val accessToken: String?,
    val refreshToken: String?,
    val tokenType: String?,
    val usuario: UsuarioResponseDTO?
)
```

#### Ejemplo de Uso
```kotlin
suspend fun login(username: String, password: String): LoginResponse {
    try {
        val request = LoginRequest(username, password)
        val response = apiService.login(request)
        
        if (response.requires2FA == true) {
            // Mostrar pantalla de 2FA
            return response
        }
        
        // Guardar tokens en DataStore
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = response.accessToken!!
            prefs[REFRESH_TOKEN_KEY] = response.refreshToken!!
            prefs[USER_ID_KEY] = response.usuario!!.id
            prefs[USERNAME_KEY] = response.usuario.username
            prefs[PUBLIC_KEY_KEY] = response.usuario.publicKey
            prefs[CERTIFICADO_KEY] = response.usuario.certificado
        }
        
        Log.d("Auth", "Login exitoso: ${response.usuario.username}")
        return response
    } catch (e: Exception) {
        Log.e("Auth", "Error en login: ${e.message}")
        throw e
    }
}
```

---

### LOGOUT

#### Endpoint
```
POST /api/auth/logout
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (200 OK)
```kotlin
data class ApiSuccessResponse(
    val success: Boolean,
    val message: String
)
```

#### Ejemplo de Uso
```kotlin
suspend fun logout() {
    try {
        val token = dataStore.data.first()[ACCESS_TOKEN_KEY]
        apiService.logout("Bearer $token")
        
        // Limpiar DataStore
        dataStore.edit { prefs ->
            prefs.clear()
        }
        
        Log.d("Auth", "Logout exitoso")
    } catch (e: Exception) {
        Log.e("Auth", "Error en logout: ${e.message}")
    }
}
```

---

### REFRESH TOKEN

#### Endpoint
```
POST /api/auth/refresh
```

#### Request
```kotlin
data class RefreshTokenRequest(
    val refreshToken: String,
    val accessToken: String
)
```

#### Response (200 OK)
```kotlin
// Mismo que LoginResponse
```

#### Ejemplo de Uso
```kotlin
suspend fun refreshAccessToken(): String? {
    try {
        val refreshToken = dataStore.data.first()[REFRESH_TOKEN_KEY]!!
        val accessToken = dataStore.data.first()[ACCESS_TOKEN_KEY]!!
        
        val request = RefreshTokenRequest(refreshToken, accessToken)
        val response = apiService.refreshToken(request)
        
        // Guardar nuevo access token
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = response.accessToken!!
        }
        
        return response.accessToken
    } catch (e: Exception) {
        Log.e("Auth", "Error refrescando token: ${e.message}")
        return null
    }
}
```

---

### OBTENER USUARIOS PÚBLICOS

#### Endpoint
```
GET /api/usuarios
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (200 OK)
```kotlin
data class UsuarioPublicoDTO(
    val id: Long,
    val username: String,
    val nombre: String,
    val publicKey: String,    // Base64
    val certificado: String   // Base64
)

// Lista de UsuarioPublicoDTO
```

#### Ejemplo de Uso
```kotlin
suspend fun obtenerUsuariosPublicos(): List<UsuarioPublicoDTO> {
    return try {
        val usuarios = apiService.getUsuariosPublicos()
        Log.d("Usuarios", "Usuarios públicos obtenidos: ${usuarios.size}")
        usuarios
    } catch (e: Exception) {
        Log.e("Usuarios", "Error obteniendo usuarios: ${e.message}")
        emptyList()
    }
}
```

---

### ACTUALIZAR CLAVE PÚBLICA

#### Endpoint
```
PUT /api/usuarios/public-key
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Request
```kotlin
data class UpdatePublicKeyRequest(
    val publicKeyBase64: String
)
```

#### Response (200 OK)
```kotlin
// UsuarioResponseDTO
```

#### Ejemplo de Uso
```kotlin
suspend fun actualizarClavePublica(publicKey: ByteArray): UsuarioResponseDTO {
    val publicKeyBase64 = Base64.encodeToString(publicKey, Base64.NO_WRAP)
    val request = UpdatePublicKeyRequest(publicKeyBase64)
    
    val response = apiService.updatePublicKey(request)
    
    // Guardar certificado
    dataStore.edit { prefs ->
        prefs[PUBLIC_KEY_KEY] = response.publicKey
        prefs[CERTIFICADO_KEY] = response.certificado
    }
    
    return response
}
```

---

## 🔒 Gestión de Secretos

### LISTAR SECRETOS

#### Endpoint
```
GET /api/secretos
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (200 OK)
```kotlin
data class SecretoSummaryDTO(
    val id: Long,
    val autorId: Long,
    val autorUsername: String,
    val autorNombre: String,
    val esAutor: Boolean,
    val cantidadCompartidos: Int
)

// Lista de SecretoSummaryDTO
```

#### Ejemplo de Uso
```kotlin
suspend fun listarSecretos(): List<SecretoSummaryDTO> {
    return try {
        val secretos = apiService.listarSecretos()
        Log.d("Secretos", "Secretos obtenidos: ${secretos.size}")
        secretos
    } catch (e: Exception) {
        Log.e("Secretos", "Error listando secretos: ${e.message}")
        emptyList()
    }
}
```

---

### CREAR SECRETO

#### Endpoint
```
POST /api/secretos
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Request
```kotlin
data class CrearSecretoRequest(
    val contenidoCifrado: String,   // Base64
    val claveAESCifrada: String,    // Base64
    val firma: String,              // Base64
    val iv: String                  // Base64
)
```

#### Response (201 Created)
```kotlin
// Long (ID del secreto creado)
```

#### Ejemplo de Uso
```kotlin
suspend fun crearSecreto(contenido: String): Long {
    // 1. Generar clave AES aleatoria
    val aesKey = generateAESKey()
    
    // 2. Cifrar contenido con AES-GCM
    val iv = ByteArray(12)
    SecureRandom().nextBytes(iv)
    val contenidoCifrado = encryptAES_GCM(contenido.toByteArray(), aesKey, iv)
    
    // 3. Cifrar clave AES con tu clave pública RSA
    val publicKey = getPublicKeyFromDataStore()
    val claveAESCifrada = encryptRSA(aesKey.encoded, publicKey)
    
    // 4. Firmar el contenido cifrado con tu clave privada
    val privateKey = getPrivateKeyFromDataStore()
    val firma = signData(contenidoCifrado, privateKey)
    
    // 5. Enviar al servidor
    val request = CrearSecretoRequest(
        contenidoCifrado = Base64.encodeToString(contenidoCifrado, Base64.NO_WRAP),
        claveAESCifrada = Base64.encodeToString(claveAESCifrada, Base64.NO_WRAP),
        firma = Base64.encodeToString(firma, Base64.NO_WRAP),
        iv = Base64.encodeToString(iv, Base64.NO_WRAP)
    )
    
    return apiService.crearSecreto(request)
}
```

---

### OBTENER SECRETO

#### Endpoint
```
GET /api/secretos/{id}
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (200 OK)
```kotlin
data class SecretoCifradoResponse(
    val id: Long,
    val autorId: Long,
    val autorUsername: String,
    val autorNombre: String,
    val contenidoCifrado: String,       // Base64
    val claveAESCifrada: String,        // Base64
    val firma: String,                  // Base64
    val iv: String,                     // Base64
    val publicKeyAutor: String,         // Base64
    val esCompartido: Boolean,
    val compartidoCon: List<UsuarioPublicoDTO>
)
```

#### Ejemplo de Uso
```kotlin
suspend fun obtenerYDescifrarSecreto(secretoId: Long): String {
    // 1. Obtener secreto del servidor
    val secreto = apiService.obtenerSecreto(secretoId)
    
    // 2. Descifrar clave AES con tu clave privada
    val privateKey = getPrivateKeyFromDataStore()
    val claveAESCifrada = Base64.decode(secreto.claveAESCifrada, Base64.DEFAULT)
    val claveAES = decryptRSA(claveAESCifrada, privateKey)
    
    // 3. Descifrar contenido con la clave AES
    val contenidoCifrado = Base64.decode(secreto.contenidoCifrado, Base64.DEFAULT)
    val iv = Base64.decode(secreto.iv, Base64.DEFAULT)
    val secretKeyAES = SecretKeySpec(claveAES, "AES")
    val contenidoPlano = decryptAES_GCM(contenidoCifrado, secretKeyAES, iv)
    
    // 4. Verificar firma del autor
    val publicKeyAutor = Base64.decode(secreto.publicKeyAutor, Base64.DEFAULT)
    val firma = Base64.decode(secreto.firma, Base64.DEFAULT)
    val firmaValida = verifySignature(contenidoCifrado, firma, publicKeyAutor)
    
    if (!firmaValida) {
        Log.w("Secretos", "⚠️ Firma inválida en secreto $secretoId")
        throw SecurityException("Firma inválida")
    }
    
    return String(contenidoPlano)
}
```

---

### COMPARTIR SECRETO

#### Endpoint
```
POST /api/secretos/{id}/compartir
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Request
```kotlin
data class CompartirSecretoRequest(
    val receptorId: Long,
    val claveAESCifradaDestinatario: String  // Base64
)
```

#### Response (200 OK)
```kotlin
// String con mensaje de éxito
```

#### Ejemplo de Uso
```kotlin
suspend fun compartirSecreto(secretoId: Long, receptorId: Long) {
    // 1. Obtener el secreto y descifrarlo
    val secreto = apiService.obtenerSecreto(secretoId)
    val privateKey = getPrivateKeyFromDataStore()
    val claveAES = decryptRSA(
        Base64.decode(secreto.claveAESCifrada, Base64.DEFAULT),
        privateKey
    )
    
    // 2. Obtener clave pública del destinatario
    val destinatario = apiService.getUsuarioPublico(receptorId)
    val publicKeyDestinatario = Base64.decode(destinatario.publicKey, Base64.DEFAULT)
    
    // 3. Re-cifrar la clave AES con la clave pública del destinatario
    val claveAESCifradaDestinatario = encryptRSA(claveAES, publicKeyDestinatario)
    
    // 4. Enviar al servidor
    val request = CompartirSecretoRequest(
        receptorId = receptorId,
        claveAESCifradaDestinatario = Base64.encodeToString(
            claveAESCifradaDestinatario,
            Base64.NO_WRAP
        )
    )
    
    apiService.compartirSecreto(secretoId, request)
    Log.d("Secretos", "Secreto $secretoId compartido con usuario $receptorId")
}
```

---

### REVOCAR ACCESO

#### Endpoint
```
DELETE /api/secretos/{id}/compartir/{receptorId}
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (204 No Content)

#### Ejemplo de Uso
```kotlin
suspend fun revocarAcceso(secretoId: Long, receptorId: Long) {
    try {
        apiService.revocarAcceso(secretoId, receptorId)
        Log.d("Secretos", "Acceso revocado exitosamente")
    } catch (e: Exception) {
        Log.e("Secretos", "Error revocando acceso: ${e.message}")
    }
}
```

---

### BORRAR SECRETO

#### Endpoint
```
DELETE /api/secretos/{id}
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (204 No Content)

#### Ejemplo de Uso
```kotlin
suspend fun borrarSecreto(secretoId: Long) {
    try {
        apiService.borrarSecreto(secretoId)
        Log.d("Secretos", "Secreto $secretoId borrado exitosamente")
    } catch (e: Exception) {
        Log.e("Secretos", "Error borrando secreto: ${e.message}")
    }
}
```

---

## 🔑 Criptografía

### OBTENER CLAVE PÚBLICA DEL SERVIDOR

#### Endpoint
```
GET /api/crypto/public-key
```

#### Headers
```kotlin
// No requiere autenticación (público)
```

#### Response (200 OK)
```kotlin
data class ClavePublicaServidorResponse(
    val publicKey: String  // Base64
)
```

#### Ejemplo de Uso
```kotlin
suspend fun obtenerClavePublicaServidor(): PublicKey {
    val response = apiService.getServerPublicKey()
    val publicKeyBytes = Base64.decode(response.publicKey, Base64.DEFAULT)
    
    val keyFactory = KeyFactory.getInstance("RSA")
    val keySpec = X509EncodedKeySpec(publicKeyBytes)
    val serverPublicKey = keyFactory.generatePublic(keySpec)
    
    // Guardar en DataStore
    dataStore.edit { prefs ->
        prefs[SERVER_PUBLIC_KEY] = publicKeyBytes
    }
    
    return serverPublicKey
}
```

---

## 🏋️ Entrenamientos y Ejercicios

### LISTAR ENTRENAMIENTOS

#### Endpoint
```
GET /api/entrenamientos
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (200 OK)
```kotlin
data class Entrenamiento(
    val id: Long?,
    val usuarioId: Long?,
    val nombre: String,
    val descripcion: String?,
    val ejercicios: List<Ejercicio>?
)

// Lista de Entrenamiento
```

---

### OBTENER ENTRENAMIENTO

#### Endpoint
```
GET /api/entrenamientos/{id}
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (200 OK)
```kotlin
// Entrenamiento
```

---

### CREAR ENTRENAMIENTO

#### Endpoint
```
POST /api/entrenamientos
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Request
```kotlin
// Entrenamiento (sin id, usuarioId se asigna automáticamente)
```

#### Response (201 Created)
```kotlin
// Entrenamiento creado
```

---

### ACTUALIZAR ENTRENAMIENTO

#### Endpoint
```
PUT /api/entrenamientos/{id}
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Request
```kotlin
// Entrenamiento
```

#### Response (200 OK)
```kotlin
// Entrenamiento actualizado
```

#### ⚠️ IMPORTANTE
```kotlin
// Si NO envías la lista de ejercicios, se mantienen los existentes
val entrenamiento = Entrenamiento(
    id = 1,
    usuarioId = null,  // Se ignora
    nombre = "Nuevo nombre",
    descripcion = "Nueva descripción",
    ejercicios = null  // ✅ Se mantienen los ejercicios existentes
)
```

---

### BORRAR ENTRENAMIENTO

#### Endpoint
```
DELETE /api/entrenamientos/{id}
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (204 No Content)

---

### LISTAR EJERCICIOS

#### Endpoint
```
GET /api/ejercicios
```

#### Headers
```kotlin
Authorization: Bearer <accessToken>
```

#### Response (200 OK)
```kotlin
data class Ejercicio(
    val id: Long?,
    val nombre: String,
    val tipoEntrenamiento: String,
    val imagenUrl: String?,
    val descripcion: String?
)

// Lista de Ejercicio
```

---

## 📦 Modelos de Datos (DTOs)

### Autenticación

```kotlin
// REGISTRO
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val nombre: String,
    val rol: String = "USER",
    val publicKeyBase64: String? = null
)

data class UsuarioResponseDTO(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val publicKey: String,
    val certificado: String,
    val rol: String
)

// LOGIN
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val requires2FA: Boolean?,
    val accessToken: String?,
    val refreshToken: String?,
    val tokenType: String?,
    val usuario: UsuarioResponseDTO?
)

// REFRESH TOKEN
data class RefreshTokenRequest(
    val refreshToken: String,
    val accessToken: String
)

// LOGOUT
data class ApiSuccessResponse(
    val success: Boolean,
    val message: String
)
```

### Usuarios

```kotlin
data class UsuarioPublicoDTO(
    val id: Long,
    val username: String,
    val nombre: String,
    val publicKey: String,    // Base64
    val certificado: String   // Base64
)

data class UpdatePublicKeyRequest(
    val publicKeyBase64: String
)
```

### Secretos

```kotlin
data class SecretoSummaryDTO(
    val id: Long,
    val autorId: Long,
    val autorUsername: String,
    val autorNombre: String,
    val esAutor: Boolean,
    val cantidadCompartidos: Int
)

data class CrearSecretoRequest(
    val contenidoCifrado: String,   // Base64
    val claveAESCifrada: String,    // Base64
    val firma: String,              // Base64
    val iv: String                  // Base64
)

data class SecretoCifradoResponse(
    val id: Long,
    val autorId: Long,
    val autorUsername: String,
    val autorNombre: String,
    val contenidoCifrado: String,       // Base64
    val claveAESCifrada: String,        // Base64
    val firma: String,                  // Base64
    val iv: String,                     // Base64
    val publicKeyAutor: String,         // Base64
    val esCompartido: Boolean,
    val compartidoCon: List<UsuarioPublicoDTO>
)

data class CompartirSecretoRequest(
    val receptorId: Long,
    val claveAESCifradaDestinatario: String  // Base64
)
```

### Criptografía

```kotlin
data class ClavePublicaServidorResponse(
    val publicKey: String  // Base64
)
```

### Entrenamientos

```kotlin
data class Entrenamiento(
    val id: Long?,
    val usuarioId: Long?,
    val nombre: String,
    val descripcion: String?,
    val ejercicios: List<Ejercicio>?
)

data class Ejercicio(
    val id: Long?,
    val nombre: String,
    val tipoEntrenamiento: String,
    val imagenUrl: String?,
    val descripcion: String?
)
```

---

## 📊 Códigos de Estado HTTP

### Éxito (2xx)

| Código | Significado | Cuándo |
|--------|-------------|--------|
| 200 | OK | Operación exitosa (GET, PUT) |
| 201 | Created | Recurso creado (POST) |
| 204 | No Content | Operación exitosa sin contenido (DELETE) |

### Errores del Cliente (4xx)

| Código | Significado | Cuándo |
|--------|-------------|--------|
| 400 | Bad Request | Datos inválidos |
| 401 | Unauthorized | No autenticado o token inválido |
| 403 | Forbidden | No tienes permisos |
| 404 | Not Found | Recurso no encontrado |

### Errores del Servidor (5xx)

| Código | Significado | Cuándo |
|--------|-------------|--------|
| 500 | Internal Server Error | Error del servidor |

---

## ⚠️ Manejo de Errores

### Estructura de Error

```kotlin
// El servidor retorna errores en este formato
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
```

### Ejemplo de Manejo

```kotlin
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> {
                // Token expirado, intentar refresh
                refreshAccessToken()
                // Reintentar la llamada
                try {
                    Result.success(apiCall())
                } catch (retryException: Exception) {
                    Result.failure(retryException)
                }
            }
            403 -> Result.failure(SecurityException("Acceso denegado"))
            404 -> Result.failure(NoSuchElementException("Recurso no encontrado"))
            else -> Result.failure(e)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// Uso
val result = safeApiCall { apiService.obtenerSecreto(1) }
result.onSuccess { secreto ->
    // Procesar secreto
}.onFailure { error ->
    Log.e("API", "Error: ${error.message}")
}
```

---

## 🔐 Parámetros Criptográficos

### Configuración Recomendada

```kotlin
object CryptoConfig {
    // RSA
    const val RSA_KEY_SIZE = 4096
    const val RSA_ALGORITHM = "RSA"
    const val RSA_CIPHER = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    
    // AES
    const val AES_KEY_SIZE = 256
    const val AES_ALGORITHM = "AES"
    const val AES_CIPHER = "AES/GCM/NoPadding"
    const val GCM_TAG_LENGTH = 128
    const val GCM_IV_LENGTH = 12
    
    // PBKDF2 (para cifrar clave privada)
    const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    const val PBKDF2_ITERATIONS = 100000
    const val PBKDF2_KEY_LENGTH = 256
    const val PBKDF2_SALT_LENGTH = 32
    
    // Firma Digital
    const val SIGNATURE_ALGORITHM = "SHA256withRSA"
}
```

---

## 🎯 Flujo Completo de Ejemplo

### Flujo: Usuario se Registra y Crea un Secreto

```kotlin
class SecretoRepository(
    private val apiService: ApiService,
    private val dataStore: DataStore<Preferences>
) {
    
    // 1. REGISTRO
    suspend fun registrarUsuario(
        username: String,
        password: String,
        email: String,
        nombre: String
    ) {
        // Generar par de claves RSA
        val keyPair = generateRSAKeyPair()
        
        // Cifrar clave privada con PBKDF2 + AES-GCM
        val (encryptedPrivateKey, salt, iv) = encryptPrivateKey(
            keyPair.private,
            password
        )
        
        // Guardar clave privada cifrada en DataStore
        dataStore.edit { prefs ->
            prefs[ENCRYPTED_PRIVATE_KEY] = Base64.encodeToString(
                encryptedPrivateKey,
                Base64.NO_WRAP
            )
            prefs[SALT_KEY] = Base64.encodeToString(salt, Base64.NO_WRAP)
            prefs[IV_KEY] = Base64.encodeToString(iv, Base64.NO_WRAP)
        }
        
        // Registrar en el servidor
        val response = apiService.register(
            RegisterRequest(
                username = username,
                password = password,
                email = email,
                nombre = nombre,
                rol = "USER",
                publicKeyBase64 = Base64.encodeToString(
                    keyPair.public.encoded,
                    Base64.NO_WRAP
                )
            )
        )
        
        // Guardar datos del usuario
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = response.id
            prefs[USERNAME_KEY] = response.username
            prefs[PUBLIC_KEY_KEY] = response.publicKey
            prefs[CERTIFICADO_KEY] = response.certificado
        }
    }
    
    // 2. LOGIN
    suspend fun login(username: String, password: String) {
        val response = apiService.login(
            LoginRequest(username, password)
        )
        
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = response.accessToken!!
            prefs[REFRESH_TOKEN_KEY] = response.refreshToken!!
        }
    }
    
    // 3. CREAR SECRETO
    suspend fun crearSecreto(contenido: String, passwordUsuario: String): Long {
        // Obtener clave privada descifrada
        val privateKey = getDecryptedPrivateKey(passwordUsuario)
        val publicKey = getPublicKey()
        
        // Generar clave AES
        val aesKey = generateAESKey()
        
        // Cifrar contenido
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val contenidoCifrado = encryptAES_GCM(
            contenido.toByteArray(),
            aesKey,
            iv
        )
        
        // Cifrar clave AES con clave pública
        val claveAESCifrada = encryptRSA(aesKey.encoded, publicKey)
        
        // Firmar contenido
        val firma = signData(contenidoCifrado, privateKey)
        
        // Enviar al servidor
        return apiService.crearSecreto(
            CrearSecretoRequest(
                contenidoCifrado = Base64.encodeToString(contenidoCifrado, Base64.NO_WRAP),
                claveAESCifrada = Base64.encodeToString(claveAESCifrada, Base64.NO_WRAP),
                firma = Base64.encodeToString(firma, Base64.NO_WRAP),
                iv = Base64.encodeToString(iv, Base64.NO_WRAP)
            )
        )
    }
    
    // 4. OBTENER Y DESCIFRAR SECRETO
    suspend fun obtenerSecreto(
        secretoId: Long,
        passwordUsuario: String
    ): String {
        // Obtener secreto del servidor
        val secreto = apiService.obtenerSecreto(secretoId)
        
        // Descifrar clave AES
        val privateKey = getDecryptedPrivateKey(passwordUsuario)
        val claveAES = decryptRSA(
            Base64.decode(secreto.claveAESCifrada, Base64.DEFAULT),
            privateKey
        )
        
        // Descifrar contenido
        val contenido = decryptAES_GCM(
            Base64.decode(secreto.contenidoCifrado, Base64.DEFAULT),
            SecretKeySpec(claveAES, "AES"),
            Base64.decode(secreto.iv, Base64.DEFAULT)
        )
        
        // Verificar firma
        val firmaValida = verifySignature(
            Base64.decode(secreto.contenidoCifrado, Base64.DEFAULT),
            Base64.decode(secreto.firma, Base64.DEFAULT),
            Base64.decode(secreto.publicKeyAutor, Base64.DEFAULT)
        )
        
        if (!firmaValida) {
            throw SecurityException("Firma inválida")
        }
        
        return String(contenido)
    }
}
```

---

## 📝 Notas Importantes

### 1. **Seguridad**
- ✅ Todas las operaciones criptográficas se hacen en el cliente
- ✅ La clave privada NUNCA se envía al servidor
- ✅ El servidor NUNCA puede descifrar los secretos
- ✅ Siempre verifica las firmas digitales

### 2. **Gestión de Tokens**
- El `accessToken` expira en 15 minutos
- El `refreshToken` expira en 7 días
- Implementa lógica para refrescar tokens automáticamente

### 3. **Almacenamiento**
- Usa `DataStore` para guardar tokens y datos del usuario
- La clave privada debe estar **siempre cifrada** en DataStore
- Nunca guardes contraseñas en texto plano

### 4. **Errores Comunes**
- 401: Token expirado → Refrescar token
- 403: Sin clave pública → Actualizar clave pública
- 404: Recurso no existe → Verificar ID
- 500: Error del servidor → Reintentar después

---

## 🚀 Swagger UI

Puedes ver toda la documentación interactiva en:

```
http://localhost:8080/swagger-ui.html
```

---

## 🎉 ¡Listo!

Tu cliente Kotlin/Android ahora tiene toda la información necesaria para conectarse al servidor y usar todas sus funcionalidades de forma segura.

**¿Necesitas más detalles sobre algún endpoint o funcionalidad específica? Consulta la documentación de Swagger o contacta al equipo de desarrollo.**

