# 📱 Documentación Completa API EmailSpring - Cliente Móvil Kotlin

**Versión**: 1.0  
**Base URL**: `http://localhost:8080`  
**Tipo**: RESTful API  
**Autenticación**: JWT (Bearer Token)

---

## 📋 Tabla de Contenidos

1. [Arquitectura General](#arquitectura-general)
2. [Autenticación y Seguridad](#autenticación-y-seguridad)
3. [Flujos Principales](#flujos-principales)
4. [Endpoints API](#endpoints-api)
5. [Modelos de Datos (DTOs)](#modelos-de-datos-dtos)
6. [Códigos de Estado HTTP](#códigos-de-estado-http)
7. [Manejo de Errores](#manejo-de-errores)
8. [Ejemplos de Implementación Kotlin](#ejemplos-de-implementación-kotlin)
9. [Configuración del Cliente](#configuración-del-cliente)

---

## 🏗️ Arquitectura General

### Stack Tecnológico del Servidor
- **Framework**: Spring Boot 4.0.2
- **Base de datos**: H2 (persistente en archivo)
- **Cache/Sesiones**: Redis (para blacklist de tokens y 2FA temporal)
- **Seguridad**: Spring Security + JWT
- **2FA**: TOTP (Google Authenticator compatible)
- **Cifrado**: RSA 2048 + AES-256-GCM + PBKDF2

### Arquitectura de 3 Capas
```
┌─────────────────────────────────────┐
│   UI Layer (Controllers + DTOs)    │
├─────────────────────────────────────┤
│   Domain Layer (Services + Models)  │
├─────────────────────────────────────┤
│   Data Layer (Repositories + JPA)   │
└─────────────────────────────────────┘
```

### Módulos del Sistema
1. **Autenticación**: Registro, login, logout, refresh tokens
2. **2FA**: TOTP con Google Authenticator
3. **Gestión de Usuarios**: Activación por email, perfiles
4. **Entrenamientos**: CRUD completo (admin) + lectura (user)
5. **Ejercicios**: Catálogo de ejercicios con imágenes
6. **Secretos Cifrados**: Sistema de cifrado end-to-end con compartición

---

## 🔐 Autenticación y Seguridad

### Sistema de Tokens JWT

#### Access Token
- **Duración**: 1 hora (3600000 ms)
- **Uso**: Autenticar cada petición a la API
- **Header**: `Authorization: Bearer {accessToken}`
- **Claims**: `sub` (username), `rol` (ADMIN/USER), `iat`, `exp`

#### Refresh Token
- **Duración**: 7 días (604800000 ms)
- **Uso**: Obtener nuevos access tokens sin re-login
- **Endpoint**: `POST /api/auth/refresh`

#### Algoritmo de Firma
```kotlin
// El servidor usa HMAC-SHA512 derivado de SHA-512
// NO necesitas replicarlo en el cliente, solo almacenar los tokens
```

### Flujo de Autenticación

```mermaid
graph TD
    A[Login] -->|Credenciales válidas| B{¿Tiene 2FA?}
    B -->|NO| C[Retorna Tokens JWT]
    B -->|SÍ| D[Retorna requires2FA=true]
    D --> E[Usuario introduce código 6 dígitos]
    E --> F[POST /api/auth/2fa/verify]
    F -->|Código válido| C
    C --> G[Guardar Access + Refresh Token]
    G --> H[Usar Access Token en Headers]
```

### Roles y Permisos

| Rol | Permisos |
|-----|----------|
| `USER` | Ver entrenamientos, ejercicios, gestionar secretos propios, configurar 2FA |
| `ADMIN` | Todo lo de USER + crear/editar/eliminar entrenamientos |

### Headers Requeridos

**Para peticiones autenticadas:**
```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
```

**Para peticiones públicas:**
```http
Content-Type: application/json
```

---

## 🔄 Flujos Principales

### 1. Registro de Usuario Nuevo

```kotlin
// Paso 1: Registro
POST /api/auth/register
Body: {
    "username": "nuevo_usuario",
    "password": "MiPassword123!",
    "email": "usuario@example.com",
    "nombre": "Juan Pérez"
}

// Respuesta 201 Created:
{
    "id": 1,
    "username": "nuevo_usuario",
    "email": "usuario@example.com",
    "nombre": "Juan Pérez",
    "clavePublica": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMI...",
    "rol": "USER"
}

// Paso 2: El servidor envía email con código de activación
// El email contiene un enlace: http://localhost:8080/api/auth/activar?codigo=UUID

// Paso 3: Usuario hace clic en el enlace (o ingresa código en la app)
GET /api/auth/activar?codigo=550e8400-e29b-41d4-a716-446655440000

// Respuesta: Página HTML de confirmación (o puedes parsearlo)
```

**Notas importantes:**
- El servidor genera automáticamente un par de claves RSA (pública/privada) para cada usuario
- La clave privada se cifra con el password del usuario usando PBKDF2
- El código de activación expira en **48 horas**
- El usuario NO puede hacer login hasta activar la cuenta

---

### 2. Login (Sin 2FA)

```kotlin
// Paso 1: Intentar login
POST /api/auth/login
Body: {
    "username": "usuario",
    "password": "MiPassword123!"
}

// Respuesta 200 OK (si NO tiene 2FA):
{
    "success": true,
    "message": "Login exitoso",
    "requires2FA": false,
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJyb2wiOiJVU0VSIiwic3ViIjoidXN1YXJpbyIsImlhdCI6MTcwODU0MzIwMCwiZXhwIjoxNzA4NTQ2ODAwfQ...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c3VhcmlvIiwiaWF0IjoxNzA4NTQzMjAwLCJleHAiOjE3MDkxNDgwMDB9...",
    "tokenType": "Bearer",
    "usuario": {
        "id": 1,
        "username": "usuario",
        "email": "usuario@example.com",
        "nombre": "Juan Pérez",
        "clavePublica": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMI...",
        "rol": "USER"
    }
}

// Paso 2: Guardar tokens en SharedPreferences o DataStore
// accessToken -> para autenticar requests
// refreshToken -> para renovar cuando expire el access
```

---

### 3. Login (Con 2FA Activado)

```kotlin
// Paso 1: Intentar login
POST /api/auth/login
Body: {
    "username": "usuario_con_2fa",
    "password": "MiPassword123!"
}

// Respuesta 202 Accepted (requiere 2FA):
{
    "success": false,
    "message": "Se requiere código de autenticación de dos factores",
    "requires2FA": true,
    "accessToken": null,
    "refreshToken": null,
    "tokenType": null,
    "usuario": null
}

// Paso 2: Mostrar pantalla para ingresar código de 6 dígitos
// El usuario abre Google Authenticator y lee el código

// Paso 3: Enviar código de verificación
POST /api/auth/2fa/verify
Body: {
    "username": "usuario_con_2fa",
    "codigo": "123456"
}

// Respuesta 200 OK (si código válido):
{
    "success": true,
    "message": "Login completado exitosamente",
    "requires2FA": false,
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "usuario": { ... }
}

// Respuesta 401 Unauthorized (si código inválido):
{
    "message": "Código de verificación inválido"
}
```

**Notas:**
- El código de 6 dígitos cambia cada 30 segundos
- Hay una ventana de tolerancia de ±30 segundos (total 90 segundos de validez)
- El servidor almacena temporalmente el estado "pendiente de 2FA" en Redis (10 min TTL)

---

### 4. Configurar 2FA (Primera Vez)

```kotlin
// REQUISITO: Usuario debe estar autenticado (tiene access token válido)

// Paso 1: Solicitar generación de secreto y QR
POST /api/auth/2fa/enable
Headers: Authorization: Bearer {accessToken}

// Respuesta 200 OK:
{
    "success": true,
    "data": {
        "secret": "JBSWY3DPEHPK3PXP",
        "qrCodeUri": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
        "message": "Escanea el código QR con tu aplicación autenticadora (Google Authenticator, Authy, etc.) y confirma con un código"
    }
}

// Paso 2: En la app móvil
// - Mostrar el QR code (decodificar base64 y mostrarlo como imagen)
// - O mostrar el secret para ingreso manual
// - Usuario escanea con Google Authenticator
// - Usuario introduce el código de 6 dígitos que aparece en la app

// Paso 3: Confirmar con código
POST /api/auth/2fa/confirm
Headers: Authorization: Bearer {accessToken}
Body: {
    "code": "123456"
}

// Respuesta 200 OK:
{
    "success": true,
    "message": "Autenticación de dos factores activada correctamente"
}

// Respuesta 400 Bad Request (código inválido):
{
    "message": "Código inválido. Verifica que tu app esté sincronizada correctamente."
}

// A partir de ahora, TODOS los logins requerirán el código 2FA
```

---

### 5. Desactivar 2FA

```kotlin
POST /api/auth/2fa/disable
Headers: Authorization: Bearer {accessToken}

// Respuesta 200 OK:
{
    "success": true,
    "message": "Autenticación de dos factores desactivada"
}
```

---

### 6. Consultar Estado 2FA

```kotlin
GET /api/auth/2fa/status
Headers: Authorization: Bearer {accessToken}

// Respuesta 200 OK:
{
    "success": true,
    "enabled": true  // o false
}
```

---

### 7. Refrescar Access Token

```kotlin
// Cuando el access token está por expirar o ya expiró
POST /api/auth/refresh
Body: {
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "accessToken": "eyJhbGciOiJIUzUxMiJ9..."  // El token viejo (para revocarlo)
}

// Respuesta 200 OK:
{
    "success": true,
    "message": "Token refrescado exitosamente",
    "requires2FA": false,
    "accessToken": "eyJhbGciOiJIUzUxMiJ9... (NUEVO)",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9... (NUEVO)",
    "tokenType": "Bearer",
    "usuario": { ... }
}

// IMPORTANTE: El servidor revoca el access token anterior
// Debes actualizar ambos tokens en el almacenamiento local
```

**Cuándo refrescar:**
- Cuando recibas un error 401 Unauthorized
- 5 minutos antes de que expire (recomendado)
- Al iniciar la app si han pasado más de 50 minutos

---

### 8. Logout

```kotlin
POST /api/auth/logout
Headers: Authorization: Bearer {accessToken}

// Respuesta 200 OK:
{
    "success": true,
    "message": "Logout exitoso"
}

// El servidor añade el access token a la blacklist de Redis
// Después de esto, el token YA NO es válido aunque no haya expirado
```

**Post-logout en el cliente:**
```kotlin
// Borrar tokens del almacenamiento local
sharedPreferences.edit().clear().apply()
// Redirigir a pantalla de login
```

---

### 9. Listar Entrenamientos

```kotlin
GET /api/entrenamientos
Headers: Authorization: Bearer {accessToken}

// Respuesta 200 OK:
[
    {
        "id": 1,
        "nombre": "Rutina de Piernas",
        "descripcion": "Entrenamiento intensivo para tren inferior",
        "ejercicios": [
            {
                "id": 1,
                "nombre": "Sentadilla",
                "tipoEntrenamiento": "Piernas",
                "descripcion": "Ejercicio básico para cuádriceps",
                "imagenUrl": "/images/ejercicios/Sentadilla.gif"
            },
            {
                "id": 2,
                "nombre": "Peso Muerto",
                "tipoEntrenamiento": "Piernas",
                "descripcion": "Trabaja toda la cadena posterior",
                "imagenUrl": "/images/ejercicios/PesoMuerto.gif"
            }
        ]
    },
    {
        "id": 2,
        "nombre": "Rutina de Pecho",
        "descripcion": "Enfoque en pectoral",
        "ejercicios": [ ... ]
    }
]
```

**Cargar imágenes:**
```kotlin
// Las URLs de imágenes son relativas, construye la URL completa:
val imageUrl = "http://localhost:8080${ejercicio.imagenUrl}"

// Usar con Coil o Glide:
Image(
    painter = rememberAsyncImagePainter(imageUrl),
    contentDescription = ejercicio.nombre
)
```

---

### 10. Obtener Entrenamiento por ID

```kotlin
GET /api/entrenamientos/1
Headers: Authorization: Bearer {accessToken}

// Respuesta 200 OK:
{
    "id": 1,
    "nombre": "Rutina de Piernas",
    "descripcion": "Entrenamiento intensivo para tren inferior",
    "ejercicios": [ ... ]
}

// Respuesta 404 Not Found:
(sin body)
```

---

### 11. Crear Entrenamiento (Solo ADMIN)

```kotlin
POST /api/entrenamientos
Headers: Authorization: Bearer {accessToken}
Body: {
    "nombre": "Rutina de Brazos",
    "descripcion": "Enfoque en bíceps y tríceps",
    "ejercicios": [
        {
            "id": 5,
            "nombre": "Curl con Barra Z",
            "tipoEntrenamiento": "Brazos",
            "descripcion": "Trabaja bíceps",
            "imagenUrl": "/images/ejercicios/BicepsBarraZ.gif"
        }
    ]
}

// Respuesta 201 Created:
{
    "id": 10,
    "nombre": "Rutina de Brazos",
    "descripcion": "Enfoque en bíceps y tríceps",
    "ejercicios": [ ... ]
}

// Respuesta 403 Forbidden (si no es admin):
{
    "message": "Acceso denegado"
}
```

---

### 12. Actualizar Entrenamiento (Solo ADMIN)

```kotlin
PUT /api/entrenamientos/10
Headers: Authorization: Bearer {accessToken}
Body: {
    "nombre": "Rutina de Brazos Avanzada",  // Nuevo nombre
    "descripcion": "Para usuarios experimentados",
    "ejercicios": [ ... ]
}

// Respuesta 200 OK:
{
    "id": 10,
    "nombre": "Rutina de Brazos Avanzada",
    "descripcion": "Para usuarios experimentados",
    "ejercicios": [ ... ]
}
```

---

### 13. Eliminar Entrenamiento (Solo ADMIN)

```kotlin
DELETE /api/entrenamientos/10
Headers: Authorization: Bearer {accessToken}

// Respuesta 204 No Content
// Sin body
```

---

### 14. Listar Ejercicios

```kotlin
GET /api/ejercicios
Headers: Authorization: Bearer {accessToken}

// Respuesta 200 OK:
[
    {
        "id": 1,
        "nombre": "Sentadilla",
        "tipoEntrenamiento": "Piernas",
        "descripcion": "Ejercicio básico para cuádriceps",
        "imagenUrl": "/images/ejercicios/Sentadilla.gif"
    },
    {
        "id": 2,
        "nombre": "Press de Banca",
        "tipoEntrenamiento": "Pecho",
        "descripcion": "Ejercicio básico para pectoral",
        "imagenUrl": "/images/ejercicios/PressBanca.gif"
    },
    // ... más ejercicios
]
```

---

### 15. Crear Secreto Cifrado

```kotlin
POST /api/secretos
Headers: Authorization: Bearer {accessToken}
Body: {
    "password": "MiPassword123!",  // Password del usuario para descifrar clave privada
    "contenido": "Este es mi mensaje super secreto"
}

// Respuesta 201 Created:
{
    "id": 1,
    "contenido": "Este es mi mensaje super secreto",
    "autor": "Juan Pérez",
    "esCompartido": false
}
```

**Cómo funciona internamente:**
1. Genera clave AES-256 aleatoria
2. Cifra el contenido con AES-GCM
3. Cifra la clave AES con la clave pública RSA del autor
4. Firma el contenido con la clave privada RSA del autor
5. Guarda: `contenidoCifrado`, `claveSimetricaCifrada`, `firma`, `iv`, `salt`

---

### 16. Ver Secreto (Propio o Compartido)

```kotlin
POST /api/secretos/1/ver
Headers: Authorization: Bearer {accessToken}
Body: {
    "password": "MiPassword123!"  // Password del usuario
}

// Respuesta 200 OK:
{
    "id": 1,
    "contenido": "Este es mi mensaje super secreto",  // ✅ DESCIFRADO
    "autor": "Autor verificado",  // ✅ FIRMA VERIFICADA
    "esCompartido": true
}

// Respuesta 401 Unauthorized (password incorrecta):
{
    "message": "Contraseña de cifrado incorrecta o clave corrupta."
}

// Respuesta 400 Bad Request (firma inválida):
{
    "message": "La firma digital no es válida. El mensaje ha sido manipulado."
}

// Respuesta 403 Forbidden (no tienes acceso):
{
    "message": "Acceso denegado"
}
```

**Cómo funciona internamente:**
1. Descifra la clave privada RSA del usuario con su password (PBKDF2)
2. Descifra la clave AES con la clave privada RSA
3. Descifra el contenido con AES-GCM
4. **VERIFICA LA FIRMA** del autor usando su clave pública RSA
5. Si la firma no coincide, lanza excepción (contenido manipulado)

---

### 17. Compartir Secreto

```kotlin
POST /api/secretos/1/compartir
Headers: Authorization: Bearer {accessToken}
Body: {
    "password": "MiPassword123!",  // Tu password
    "receptorId": 5  // ID del usuario destinatario
}

// Respuesta 200 OK:
"Secreto compartido con éxito"
```

**Cómo funciona (Re-wrapping de clave):**
1. Descifra la clave AES del secreto con TU clave privada
2. Cifra la MISMA clave AES con la clave pública del destinatario
3. Guarda el mapeo en `secretos_compartidos`
4. El destinatario ahora puede descifrar el secreto con SU clave privada

**Seguridad:**
- El contenido NO se descifra ni re-cifra (mantiene integridad)
- Solo se re-envuelve la clave de cifrado
- La firma del autor original se mantiene intacta

---

### 18. Revocar Acceso a Secreto

```kotlin
DELETE /api/secretos/1/compartir/5
Headers: Authorization: Bearer {accessToken}

// Respuesta 204 No Content
// Elimina el registro de secretos_compartidos
// El usuario 5 ya NO puede acceder al secreto
```

---

## 📦 Modelos de Datos (DTOs)

### UsuarioDTO (Registro)
```kotlin
data class UsuarioDTO(
    val username: String,      // Requerido, único
    val password: String,      // Requerido, mínimo 8 caracteres
    val email: String,         // Requerido, formato email
    val nombre: String,        // Requerido
    val rol: String? = null    // Opcional, por defecto "USER"
)
```

### UsuarioResponseDTO
```kotlin
data class UsuarioResponseDTO(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val clavePublica: String,  // Base64 encoded RSA public key
    val rol: String            // "USER" o "ADMIN"
)
```

### LoginRequest
```kotlin
data class LoginRequest(
    val username: String,
    val password: String
)
```

### LoginResponse
```kotlin
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val requires2FA: Boolean?,      // true si necesita verificación 2FA
    val accessToken: String?,       // null si requires2FA=true
    val refreshToken: String?,      // null si requires2FA=true
    val tokenType: String?,         // "Bearer"
    val usuario: UsuarioResponseDTO?  // null si requires2FA=true
)
```

### RefreshTokenRequest
```kotlin
data class RefreshTokenRequest(
    val refreshToken: String,
    val accessToken: String  // El token viejo a revocar
)
```

### Verify2FALoginRequest
```kotlin
data class Verify2FALoginRequest(
    val username: String,
    val codigo: String  // Código de 6 dígitos
)
```

### Confirm2FARequest
```kotlin
data class Confirm2FARequest(
    val code: String  // Código de 6 dígitos
)
```

### Enable2FADataResponse
```kotlin
data class Enable2FADataResponse(
    val success: Boolean,
    val data: Enable2FAResponse
)

data class Enable2FAResponse(
    val secret: String,        // "JBSWY3DPEHPK3PXP" (para ingreso manual)
    val qrCodeUri: String,     // "data:image/png;base64,iVBORw0KGg..."
    val message: String
)
```

### TwoFactorStatusResponse
```kotlin
data class TwoFactorStatusResponse(
    val success: Boolean,
    val enabled: Boolean  // true si 2FA está activado
)
```

### ApiSuccessResponse
```kotlin
data class ApiSuccessResponse(
    val success: Boolean,
    val message: String
)
```

### Entrenamiento
```kotlin
data class Entrenamiento(
    val id: Long?,
    val nombre: String,
    val descripcion: String?,
    val ejercicios: List<Ejercicio>
)
```

### Ejercicio
```kotlin
data class Ejercicio(
    val id: Long?,
    val nombre: String,
    val tipoEntrenamiento: String,  // "Piernas", "Pecho", "Espalda", "Brazos", "Hombros"
    val descripcion: String?,
    val imagenUrl: String  // "/images/ejercicios/Sentadilla.gif"
)
```

### SecretoRequest
```kotlin
data class SecretoRequest(
    val password: String,   // Password del usuario
    val contenido: String   // Texto a cifrar
)
```

### VerSecretoRequest
```kotlin
data class VerSecretoRequest(
    val password: String  // Password del usuario
)
```

### CompartirRequest
```kotlin
data class CompartirRequest(
    val password: String,  // Password del usuario
    val receptorId: Long   // ID del usuario destinatario
)
```

### SecretoResponse
```kotlin
data class SecretoResponse(
    val id: Long,
    val contenido: String,     // Contenido descifrado
    val autor: String,         // Nombre del autor
    val esCompartido: Boolean  // true si te lo compartieron
)
```

---

## 🚦 Códigos de Estado HTTP

| Código | Significado | Cuándo se usa |
|--------|-------------|---------------|
| **200** | OK | Petición exitosa (GET, POST con respuesta) |
| **201** | Created | Recurso creado exitosamente (POST registro, crear entrenamiento) |
| **202** | Accepted | Login requiere 2FA |
| **204** | No Content | Eliminación exitosa (DELETE) |
| **400** | Bad Request | Datos inválidos, código 2FA incorrecto, código activación inválido |
| **401** | Unauthorized | Token inválido/expirado, credenciales incorrectas, password cifrado incorrecta |
| **403** | Forbidden | Sin permisos (USER intentando acción de ADMIN) |
| **404** | Not Found | Recurso no encontrado |
| **500** | Internal Server Error | Error del servidor |

---

## ⚠️ Manejo de Errores

### Formato de Respuesta de Error

```kotlin
// Respuesta de error genérica
{
    "message": "Descripción del error"
}

// Ejemplo 401:
{
    "message": "Credenciales inválidas"
}

// Ejemplo 403:
{
    "message": "Acceso denegado"
}

// Ejemplo 400:
{
    "message": "Código inválido. Verifica que tu app esté sincronizada correctamente."
}
```

### Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `"Credenciales inválidas"` | Username/password incorrectos | Verificar datos de login |
| `"Usuario no encontrado"` | Username no existe | Verificar que el usuario esté registrado |
| `"Refresh token inválido o expirado"` | Token expirado o revocado | Hacer login nuevamente |
| `"Token revocado intentando acceder"` | Token en blacklist | Hacer login nuevamente |
| `"Código de verificación inválido"` | Código 2FA incorrecto | Verificar sincronización de hora |
| `"Contraseña de cifrado incorrecta o clave corrupta"` | Password incorrecta al descifrar | Verificar password del usuario |
| `"La firma digital no es válida. El mensaje ha sido manipulado."` | Secreto manipulado | Alertar al usuario, no mostrar contenido |
| `"Acceso denegado"` | Sin permisos o secreto no compartido | Verificar rol o acceso |
| `"El código de activación ha expirado."` | >48h desde registro | Solicitar nuevo código |

### Interceptor de Errores Recomendado (Kotlin)

```kotlin
class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val onUnauthorized: () -> Unit
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Añadir token si existe
        val token = tokenManager.getAccessToken()
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        val response = chain.proceed(request)
        
        // Manejar 401 Unauthorized
        if (response.code == 401) {
            response.close()
            
            // Intentar refresh
            val refreshed = tryRefreshToken()
            if (refreshed) {
                // Reintentar request original con nuevo token
                val newToken = tokenManager.getAccessToken()
                val retryRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
                return chain.proceed(retryRequest)
            } else {
                // Refresh falló, logout
                onUnauthorized()
                return response
            }
        }
        
        return response
    }
    
    private fun tryRefreshToken(): Boolean {
        // Implementar lógica de refresh
        return false
    }
}
```

---

## 💻 Ejemplos de Implementación Kotlin

### 1. Configuración de Retrofit

```kotlin
// ApiConfig.kt
object ApiConfig {
    private const val BASE_URL = "http://10.0.2.2:8080/" // Android emulator
    // private const val BASE_URL = "http://localhost:8080/" // iOS simulator
    // private const val BASE_URL = "http://192.168.1.100:8080/" // Dispositivo real
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

### 2. Interface de API

```kotlin
// ApiService.kt
interface ApiService {
    
    // Autenticación
    @POST("api/auth/register")
    suspend fun register(@Body request: UsuarioDTO): Response<UsuarioResponseDTO>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiSuccessResponse>
    
    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>
    
    // 2FA
    @POST("api/auth/2fa/enable")
    suspend fun enable2FA(): Response<Enable2FADataResponse>
    
    @POST("api/auth/2fa/confirm")
    suspend fun confirm2FA(@Body request: Confirm2FARequest): Response<ApiSuccessResponse>
    
    @POST("api/auth/2fa/disable")
    suspend fun disable2FA(): Response<ApiSuccessResponse>
    
    @POST("api/auth/2fa/verify")
    suspend fun verify2FA(@Body request: Verify2FALoginRequest): Response<LoginResponse>
    
    @GET("api/auth/2fa/status")
    suspend fun get2FAStatus(): Response<TwoFactorStatusResponse>
    
    // Entrenamientos
    @GET("api/entrenamientos")
    suspend fun getEntrenamientos(): Response<List<Entrenamiento>>
    
    @GET("api/entrenamientos/{id}")
    suspend fun getEntrenamiento(@Path("id") id: Long): Response<Entrenamiento>
    
    @POST("api/entrenamientos")
    suspend fun createEntrenamiento(@Body entrenamiento: Entrenamiento): Response<Entrenamiento>
    
    @PUT("api/entrenamientos/{id}")
    suspend fun updateEntrenamiento(
        @Path("id") id: Long,
        @Body entrenamiento: Entrenamiento
    ): Response<Entrenamiento>
    
    @DELETE("api/entrenamientos/{id}")
    suspend fun deleteEntrenamiento(@Path("id") id: Long): Response<Unit>
    
    // Ejercicios
    @GET("api/ejercicios")
    suspend fun getEjercicios(): Response<List<Ejercicio>>
    
    // Secretos
    @POST("api/secretos")
    suspend fun createSecreto(@Body request: SecretoRequest): Response<SecretoResponse>
    
    @POST("api/secretos/{id}/ver")
    suspend fun verSecreto(
        @Path("id") id: Long,
        @Body request: VerSecretoRequest
    ): Response<SecretoResponse>
    
    @POST("api/secretos/{id}/compartir")
    suspend fun compartirSecreto(
        @Path("id") id: Long,
        @Body request: CompartirRequest
    ): Response<String>
    
    @DELETE("api/secretos/{id}/compartir/{receptorId}")
    suspend fun revocarSecreto(
        @Path("id") id: Long,
        @Path("receptorId") receptorId: Long
    ): Response<Unit>
}
```

### 3. Repository Pattern

```kotlin
// AuthRepository.kt
class AuthRepository(private val api: ApiService) {
    
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verify2FA(username: String, code: String): Result<LoginResponse> {
        return try {
            val response = api.verify2FA(Verify2FALoginRequest(username, code))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            val json = JSONObject(errorBody ?: "{}")
            json.getString("message")
        } catch (e: Exception) {
            "Error desconocido"
        }
    }
}
```

### 4. ViewModel con StateFlow

```kotlin
// LoginViewModel.kt
class LoginViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            val result = repository.login(username, password)
            result.fold(
                onSuccess = { response ->
                    when {
                        response.requires2FA == true -> {
                            _loginState.value = LoginState.Requires2FA(username)
                        }
                        response.accessToken != null -> {
                            tokenManager.saveTokens(
                                response.accessToken,
                                response.refreshToken!!
                            )
                            _loginState.value = LoginState.Success(response.usuario!!)
                        }
                        else -> {
                            _loginState.value = LoginState.Error("Respuesta inválida")
                        }
                    }
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }
    
    fun verify2FA(username: String, code: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            val result = repository.verify2FA(username, code)
            result.fold(
                onSuccess = { response ->
                    tokenManager.saveTokens(
                        response.accessToken!!,
                        response.refreshToken!!
                    )
                    _loginState.value = LoginState.Success(response.usuario!!)
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Código inválido")
                }
            )
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Requires2FA(val username: String) : LoginState()
    data class Success(val usuario: UsuarioResponseDTO) : LoginState()
    data class Error(val message: String) : LoginState()
}
```

### 5. TokenManager con DataStore

```kotlin
// TokenManager.kt
class TokenManager(private val context: Context) {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
    }
    
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }
    
    suspend fun saveUser(id: Long, username: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
            preferences[USERNAME_KEY] = username
        }
    }
    
    fun getAccessToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }
    
    fun getRefreshToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }
    
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")
```

### 6. Pantalla de Login con Jetpack Compose

```kotlin
// LoginScreen.kt
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var twoFactorCode by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = loginState) {
            is LoginState.Idle -> {
                LoginForm(
                    username = username,
                    password = password,
                    onUsernameChange = { username = it },
                    onPasswordChange = { password = it },
                    onLoginClick = { viewModel.login(username, password) }
                )
            }
            
            is LoginState.Loading -> {
                CircularProgressIndicator()
            }
            
            is LoginState.Requires2FA -> {
                TwoFactorForm(
                    code = twoFactorCode,
                    onCodeChange = { twoFactorCode = it },
                    onVerifyClick = { 
                        viewModel.verify2FA(state.username, twoFactorCode) 
                    }
                )
            }
            
            is LoginState.Success -> {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                }
            }
            
            is LoginState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                LoginForm(
                    username = username,
                    password = password,
                    onUsernameChange = { username = it },
                    onPasswordChange = { password = it },
                    onLoginClick = { viewModel.login(username, password) }
                )
            }
        }
    }
}

@Composable
fun LoginForm(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        label = { Text("Usuario") },
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Button(
        onClick = onLoginClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Iniciar Sesión")
    }
}

@Composable
fun TwoFactorForm(
    code: String,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit
) {
    Text(
        text = "Introduce el código de Google Authenticator",
        style = MaterialTheme.typography.titleMedium
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    OutlinedTextField(
        value = code,
        onValueChange = { if (it.length <= 6) onCodeChange(it) },
        label = { Text("Código 6 dígitos") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Button(
        onClick = onVerifyClick,
        enabled = code.length == 6,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Verificar")
    }
}
```

### 7. Mostrar QR Code para 2FA

```kotlin
// Enable2FAScreen.kt
@Composable
fun Enable2FAScreen(
    viewModel: TwoFactorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var confirmCode by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val currentState = state) {
            is TwoFactorState.Idle -> {
                Button(onClick = { viewModel.enable2FA() }) {
                    Text("Activar 2FA")
                }
            }
            
            is TwoFactorState.QRCodeGenerated -> {
                Text("Escanea este código QR con Google Authenticator")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Decodificar y mostrar QR
                val qrBitmap = decodeBase64ToImage(currentState.data.qrCodeUri)
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(300.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("O ingresa manualmente:")
                Text(
                    text = currentState.data.secret,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = confirmCode,
                    onValueChange = { if (it.length <= 6) confirmCode = it },
                    label = { Text("Código de verificación") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { viewModel.confirm2FA(confirmCode) },
                    enabled = confirmCode.length == 6
                ) {
                    Text("Confirmar")
                }
            }
            
            is TwoFactorState.Enabled -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(64.dp)
                )
                Text("2FA activado correctamente")
            }
            
            is TwoFactorState.Error -> {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun decodeBase64ToImage(dataUri: String): Bitmap {
    // Extraer base64 (después de "data:image/png;base64,")
    val base64String = dataUri.substringAfter("base64,")
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}
```

### 8. Cargar y Mostrar Entrenamientos

```kotlin
// EntrenamientosScreen.kt
@Composable
fun EntrenamientosScreen(
    viewModel: EntrenamientosViewModel = viewModel()
) {
    val entrenamientos by viewModel.entrenamientos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadEntrenamientos()
    }
    
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(entrenamientos) { entrenamiento ->
                EntrenamientoCard(entrenamiento)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EntrenamientoCard(entrenamiento: Entrenamiento) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entrenamiento.nombre,
                style = MaterialTheme.typography.titleLarge
            )
            
            entrenamiento.descripcion?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Ejercicios:",
                style = MaterialTheme.typography.titleSmall
            )
            
            entrenamiento.ejercicios.forEach { ejercicio ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cargar imagen del ejercicio
                    AsyncImage(
                        model = "http://10.0.2.2:8080${ejercicio.imagenUrl}",
                        contentDescription = ejercicio.nombre,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column {
                        Text(
                            text = ejercicio.nombre,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = ejercicio.tipoEntrenamiento,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
```

---

## 🔧 Configuración del Cliente

### 1. Dependencias Gradle (build.gradle.kts)

```kotlin
dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    
    // Coil (carga de imágenes)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // QR Code (opcional, si quieres generar QR)
    implementation("com.google.zxing:core:3.5.2")
}
```

### 2. Permisos AndroidManifest.xml

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        android:usesCleartextTraffic="true"
        ...>
        ...
    </application>
</manifest>
```

### 3. Network Security Config (para HTTP en desarrollo)

**res/xml/network_security_config.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">192.168.1.100</domain>
    </domain-config>
</network-security-config>
```

**AndroidManifest.xml:**
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

---

## 📝 Notas Importantes

### URLs según el dispositivo

| Dispositivo | Base URL |
|-------------|----------|
| Emulador Android | `http://10.0.2.2:8080/` |
| Simulador iOS | `http://localhost:8080/` |
| Dispositivo real (misma red WiFi) | `http://192.168.1.X:8080/` |

### Sincronización de Hora (2FA)

El código TOTP depende de la hora del sistema. Asegúrate de que:
- El dispositivo tenga la hora correcta
- Esté sincronizado con NTP
- Usa la ventana de tolerancia de ±30 segundos del servidor

### Almacenamiento Seguro

**NUNCA almacenes:**
- Passwords en SharedPreferences/DataStore
- Claves privadas RSA sin cifrar
- Secretos descifrados en memoria permanente

**SÍ almacena:**
- Access token (encriptado con EncryptedSharedPreferences)
- Refresh token (encriptado)
- Datos de usuario públicos (id, username, rol)

### Renovación Automática de Tokens

Implementa un mecanismo para renovar el access token **antes** de que expire:

```kotlin
// Cada 50 minutos (el token dura 60)
workManager.enqueue(
    PeriodicWorkRequestBuilder<RefreshTokenWorker>(50, TimeUnit.MINUTES)
        .build()
)
```

### Testing del Servidor

Antes de iniciar el desarrollo del cliente, verifica que el servidor esté funcionando:

```bash
# Verificar salud del servidor
curl http://localhost:8080/actuator/health

# Login de prueba
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

---

## 🎯 Checklist de Implementación

### Fase 1: Autenticación Básica
- [ ] Configurar Retrofit + OkHttp
- [ ] Implementar login sin 2FA
- [ ] Implementar registro
- [ ] Implementar logout
- [ ] Guardar tokens en DataStore
- [ ] Implementar refresh token automático

### Fase 2: 2FA
- [ ] Pantalla de activación 2FA
- [ ] Mostrar QR code
- [ ] Pantalla de verificación 2FA en login
- [ ] Input de código de 6 dígitos
- [ ] Manejo de estado "requiere 2FA"

### Fase 3: Entrenamientos
- [ ] Listar entrenamientos
- [ ] Ver detalles de entrenamiento
- [ ] Cargar imágenes de ejercicios
- [ ] (Si es admin) Crear/editar/eliminar entrenamientos

### Fase 4: Secretos Cifrados
- [ ] Crear secreto cifrado
- [ ] Ver secreto descifrado
- [ ] Listar secretos propios y compartidos
- [ ] Compartir secreto con otro usuario
- [ ] Revocar acceso a secreto

### Fase 5: Mejoras
- [ ] Manejo de errores global
- [ ] Retry automático en fallos de red
- [ ] Cache local de datos
- [ ] Modo offline
- [ ] Notificaciones push (si se implementa en el servidor)

---

## 📞 Contacto y Soporte

**Documentación Swagger UI**: `http://localhost:8080/swagger-ui.html`  
**H2 Console**: `http://localhost:8080/h2-console` (usuario: `root`, password: `root`)

---

**¡Éxito con el desarrollo del cliente móvil!** 🚀

