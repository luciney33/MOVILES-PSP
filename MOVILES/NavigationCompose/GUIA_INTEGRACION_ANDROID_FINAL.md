# 📱 Guía Completa de Integración - Cliente Android con Backend Seguro

## 🎯 Arquitectura del Sistema

### Backend (Zero-Knowledge Server)
El backend está implementado como un **servidor de conocimiento cero** donde:
- ✅ **NUNCA** genera claves privadas
- ✅ **NUNCA** almacena claves privadas
- ✅ **NUNCA** descifra secretos
- ✅ **SOLO** almacena datos completamente cifrados
- ✅ **VALIDA** claves públicas mediante certificados firmados

### Cliente Android (Responsable de Seguridad)
El cliente es el único responsable de:
- ✅ Generar par de claves RSA (4096 bits)
- ✅ Cifrar/descifrar su clave privada localmente (almacenar en DataStore cifrada)
- ✅ Cifrar contenido de secretos (AES-256-GCM)
- ✅ Firmar datos con su clave privada
- ✅ Verificar firmas de otros usuarios
- ✅ Gestionar todo el cifrado end-to-end

---

## 📋 Tabla de Contenidos

1. [Endpoints del Servidor](#-endpoints-del-servidor)
2. [Flujos Completos](#-flujos-completos)
3. [Implementación Criptográfica](#-implementación-criptográfica)
4. [Casos de Uso](#-casos-de-uso)

---

## 🔐 ENDPOINTS DEL SERVIDOR

### 1. **Registro** 
**POST** `/api/auth/register`

**Request:**
```json
{
  "username": "john_doe",
  "password": "SecurePass123!",
  "email": "john@example.com",
  "nombre": "John Doe",
  "rol": "USER",
  "publicKeyBase64": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..." // OBLIGATORIO
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "nombre": "John Doe",
  "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
  "certificado": "j8fNm3kL9pQxR2tY5wZ8vA2bN4cP6dQ1eR7fT9gH3iJ5kL...",
  "rol": "USER"
}
```

**Notas:**
- La clave pública es **obligatoria**
- El servidor firma la clave pública y devuelve un certificado
- El servidor **NO** almacena la clave privada

---

### 2. **Login**
**POST** `/api/auth/login`

**Request:**
```json
{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "nombre": "John Doe",
    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
    "certificado": "j8fNm3kL9pQxR2tY5wZ8vA2bN4cP6dQ1eR7fT9gH3iJ5kL...",
    "rol": "USER"
  }
}
```

---

### 3. **Obtener Usuarios Públicos**
**GET** `/api/usuarios/publicos`

**Headers:**
```
Authorization: Bearer <accessToken>
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "username": "jane_doe",
    "nombre": "Jane Doe",
    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
    "certificado": "xY3wZ9aB2cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ..."
  },
  {
    "id": 3,
    "username": "bob_smith",
    "nombre": "Bob Smith",
    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
    "certificado": "a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6q7R8s9T0u1V2..."
  }
]
```

**Uso:** 
- Obtener lista de usuarios para compartir secretos
- Cada usuario incluye su clave pública y certificado firmado

---

### 4. **Listar Secretos**
**GET** `/api/secretos`

**Headers:**
```
Authorization: Bearer <accessToken>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "autorId": 1,
    "autorUsername": "john_doe",
    "autorNombre": "John Doe",
    "esAutor": true,
    "cantidadCompartidos": 2
  },
  {
    "id": 5,
    "autorId": 3,
    "autorUsername": "bob_smith",
    "autorNombre": "Bob Smith",
    "esAutor": false,
    "cantidadCompartidos": 0
  }
]
```

**Notas:**
- Retorna secretos propios (esAutor=true) y compartidos (esAutor=false)
- **NO** incluye el contenido cifrado (solo metadata)
- Si `esAutor=false`, `cantidadCompartidos` siempre es 0

---

### 5. **Crear Secreto**
**POST** `/api/secretos`

**Headers:**
```
Authorization: Bearer <accessToken>
```

**Request:**
```json
{
  "contenidoCifrado": "aXN0byBlcyBlbCBjb250ZW5pZG8gY2lmcmFkbyBlbiBCYXNlNjQ=",
  "claveAESCifrada": "a2V5IGVuY3J5cHRlZCB3aXRoIFJTQSBwdWJsaWMga2V5",
  "firma": "ZmlybWEgZGlnaXRhbCBlbiBCYXNlNjQ=",
  "iv": "aXYgZW4gQmFzZTY0"
}
```

**Response (201 Created):**
```json
1
```

**Notas:**
- El backend **NO** requiere contraseña para crear secretos
- Todo el contenido ya viene cifrado desde el cliente
- Retorna el ID del secreto creado

---

### 6. **Obtener Secreto**
**GET** `/api/secretos/{id}`

**Headers:**
```
Authorization: Bearer <accessToken>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "autorId": 1,
  "autorUsername": "john_doe",
  "autorNombre": "John Doe",
  "contenidoCifrado": "aXN0byBlcyBlbCBjb250ZW5pZG8gY2lmcmFkbyBlbiBCYXNlNjQ=",
  "claveAESCifrada": "a2V5IGVuY3J5cHRlZCB3aXRoIFJTQSBwdWJsaWMga2V5",
  "firma": "ZmlybWEgZGlnaXRhbCBlbiBCYXNlNjQ=",
  "iv": "aXYgZW4gQmFzZTY0",
  "publicKeyAutor": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...",
  "esCompartido": false,
  "compartidoCon": [
    {
      "id": 2,
      "username": "jane_doe",
      "nombre": "Jane Doe",
      "publicKey": null,
      "certificado": null
    },
    {
      "id": 3,
      "username": "bob_smith",
      "nombre": "Bob Smith",
      "publicKey": null,
      "certificado": null
    }
  ]
}
```

**Notas:**
- Si eres el **autor** (`esCompartido=false`), recibes la lista `compartidoCon`
- Si es un **secreto compartido** contigo (`esCompartido=true`), `compartidoCon` estará vacío
- `claveAESCifrada` es la clave cifrada con TU clave pública (si eres autor) o la re-cifrada para ti (si es compartido)
- El backend **NO** requiere contraseña para ver secretos

---

### 7. **Compartir Secreto**
**POST** `/api/secretos/{id}/compartir`

**Headers:**
```
Authorization: Bearer <accessToken>
```

**Request:**
```json
{
  "receptorId": 2,
  "claveAESCifradaDestinatario": "a2V5IGVuY3J5cHRlZCBmb3IgamFuZV9kb2U="
}
```

**Response (200 OK):**
```json
"Secreto compartido exitosamente"
```

**Notas:**
- Solo el **autor** puede compartir un secreto
- El cliente debe:
  1. Obtener el secreto y descifrar la clave AES con su clave privada
  2. Obtener la clave pública del destinatario
  3. Re-cifrar la clave AES con la clave pública del destinatario
  4. Enviar la clave re-cifrada al servidor

---

### 8. **Revocar Acceso**
**DELETE** `/api/secretos/{id}/compartir/{receptorId}`

**Headers:**
```
Authorization: Bearer <accessToken>
```

**Response (204 No Content)**

**Notas:**
- Solo el **autor** puede revocar acceso
- El destinatario ya no podrá ver el secreto

---

### 9. **Borrar Secreto**
**DELETE** `/api/secretos/{id}`

**Headers:**
```
Authorization: Bearer <accessToken>
```

**Response (204 No Content)**

**Notas:**
- Solo el **autor** puede borrar un secreto
- Se eliminan automáticamente todos los compartidos

---

## 🔄 FLUJOS COMPLETOS

### Flujo 1: Registro de Usuario

```kotlin
// 1. Generar par de claves RSA (4096 bits)
val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
keyPairGenerator.initialize(4096)
val keyPair = keyPairGenerator.generateKeyPair()
val publicKey = keyPair.public
val privateKey = keyPair.private

// 2. Convertir clave pública a Base64
val publicKeyBytes = publicKey.encoded
val publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes)

// 3. Cifrar la clave privada localmente
val privateKeyBytes = privateKey.encoded
val userPassword = "contraseña_del_usuario" // La que ingresa el usuario

// Derivar clave de cifrado con PBKDF2
val salt = ByteArray(32)
SecureRandom().nextBytes(salt)
val keySpec = PBEKeySpec(userPassword.toCharArray(), salt, 100000, 256)
val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
val encryptionKey = secretKeyFactory.generateSecret(keySpec).encoded

// Cifrar clave privada con AES-GCM
val cipher = Cipher.getInstance("AES/GCM/NoPadding")
val iv = ByteArray(12)
SecureRandom().nextBytes(iv)
val gcmSpec = GCMParameterSpec(128, iv)
val secretKey = SecretKeySpec(encryptionKey, "AES")
cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
val encryptedPrivateKey = cipher.doFinal(privateKeyBytes)

// 4. Guardar en DataStore (cifrada)
dataStore.edit { prefs ->
    prefs[ENCRYPTED_PRIVATE_KEY] = Base64.getEncoder().encodeToString(encryptedPrivateKey)
    prefs[SALT_KEY] = Base64.getEncoder().encodeToString(salt)
    prefs[IV_KEY] = Base64.getEncoder().encodeToString(iv)
}

// 5. Registrar en el servidor
val registerRequest = RegisterRequest(
    username = "john_doe",
    password = "SecurePass123!",
    email = "john@example.com",
    nombre = "John Doe",
    rol = "USER",
    publicKeyBase64 = publicKeyBase64
)

val response = apiService.register(registerRequest)
// response incluye: id, username, publicKey, certificado, etc.

// 6. Guardar certificado en DataStore
dataStore.edit { prefs ->
    prefs[CERTIFICADO_KEY] = response.certificado
    prefs[PUBLIC_KEY] = response.publicKey
}
```

---

### Flujo 2: Login

```kotlin
// 1. Login en el servidor
val loginRequest = LoginRequest(
    username = "john_doe",
    password = "SecurePass123!"
)

val response = apiService.login(loginRequest)

// 2. Guardar tokens y datos del usuario
dataStore.edit { prefs ->
    prefs[ACCESS_TOKEN] = response.accessToken
    prefs[REFRESH_TOKEN] = response.refreshToken
    prefs[USER_ID] = response.usuario.id
    prefs[USERNAME] = response.usuario.username
    prefs[PUBLIC_KEY] = response.usuario.publicKey
    prefs[CERTIFICADO_KEY] = response.usuario.certificado
}

// 3. Obtener usuarios públicos (para futuras operaciones)
val usuariosPublicos = apiService.getUsuariosPublicos()
// Guardar en base de datos local o caché
```

---

### Flujo 3: Crear un Secreto

```kotlin
// 1. Obtener la clave privada descifrada del DataStore
val encryptedPrivateKeyB64 = dataStore.data.first()[ENCRYPTED_PRIVATE_KEY]!!
val saltB64 = dataStore.data.first()[SALT_KEY]!!
val ivB64 = dataStore.data.first()[IV_KEY]!!
val userPassword = getUserPassword() // Solicitar al usuario

val encryptedPrivateKey = Base64.getDecoder().decode(encryptedPrivateKeyB64)
val salt = Base64.getDecoder().decode(saltB64)
val iv = Base64.getDecoder().decode(ivB64)

// Derivar clave de descifrado
val keySpec = PBEKeySpec(userPassword.toCharArray(), salt, 100000, 256)
val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
val encryptionKey = secretKeyFactory.generateSecret(keySpec).encoded

// Descifrar clave privada
val cipher = Cipher.getInstance("AES/GCM/NoPadding")
val gcmSpec = GCMParameterSpec(128, iv)
val secretKey = SecretKeySpec(encryptionKey, "AES")
cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
val privateKeyBytes = cipher.doFinal(encryptedPrivateKey)

val keyFactory = KeyFactory.getInstance("RSA")
val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
val privateKey = keyFactory.generatePrivate(privateKeySpec)

// 2. Generar clave AES aleatoria
val aesKeyGen = KeyGenerator.getInstance("AES")
aesKeyGen.init(256)
val aesKey = aesKeyGen.generateKey()

// 3. Cifrar el contenido del secreto con AES-GCM
val contenidoPlano = "Mi secreto super importante"
val ivAES = ByteArray(12)
SecureRandom().nextBytes(ivAES)

val cipherAES = Cipher.getInstance("AES/GCM/NoPadding")
val gcmSpecAES = GCMParameterSpec(128, ivAES)
cipherAES.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpecAES)
val contenidoCifrado = cipherAES.doFinal(contenidoPlano.toByteArray())

// 4. Cifrar la clave AES con tu clave pública RSA
val publicKeyB64 = dataStore.data.first()[PUBLIC_KEY]!!
val publicKeyBytes = Base64.getDecoder().decode(publicKeyB64)
val keyFactory2 = KeyFactory.getInstance("RSA")
val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
val publicKey = keyFactory2.generatePublic(publicKeySpec)

val cipherRSA = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey)
val claveAESCifrada = cipherRSA.doFinal(aesKey.encoded)

// 5. Firmar el contenido cifrado con tu clave privada
val signature = Signature.getInstance("SHA256withRSA")
signature.initSign(privateKey)
signature.update(contenidoCifrado)
val firma = signature.sign()

// 6. Enviar al servidor
val request = CrearSecretoRequest(
    contenidoCifrado = Base64.getEncoder().encodeToString(contenidoCifrado),
    claveAESCifrada = Base64.getEncoder().encodeToString(claveAESCifrada),
    firma = Base64.getEncoder().encodeToString(firma),
    iv = Base64.getEncoder().encodeToString(ivAES)
)

val secretoId = apiService.crearSecreto(request)
// secretoId = 1
```

---

### Flujo 4: Ver un Secreto

```kotlin
// 1. Obtener el secreto del servidor
val secreto = apiService.obtenerSecreto(secretoId = 1)

// 2. Descifrar la clave AES con tu clave privada
val privateKey = getPrivateKeyFromDataStore() // Similar al Flujo 3

val claveAESCifrada = Base64.getDecoder().decode(secreto.claveAESCifrada)
val cipherRSA = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
cipherRSA.init(Cipher.DECRYPT_MODE, privateKey)
val claveAES = cipherRSA.doFinal(claveAESCifrada)

// 3. Descifrar el contenido con la clave AES
val contenidoCifrado = Base64.getDecoder().decode(secreto.contenidoCifrado)
val iv = Base64.getDecoder().decode(secreto.iv)

val secretKeyAES = SecretKeySpec(claveAES, "AES")
val cipherAES = Cipher.getInstance("AES/GCM/NoPadding")
val gcmSpec = GCMParameterSpec(128, iv)
cipherAES.init(Cipher.DECRYPT_MODE, secretKeyAES, gcmSpec)
val contenidoPlano = String(cipherAES.doFinal(contenidoCifrado))

// 4. Verificar firma del autor
val publicKeyAutor = Base64.getDecoder().decode(secreto.publicKeyAutor)
val keyFactory = KeyFactory.getInstance("RSA")
val publicKeySpec = X509EncodedKeySpec(publicKeyAutor)
val publicKey = keyFactory.generatePublic(publicKeySpec)

val signature = Signature.getInstance("SHA256withRSA")
signature.initVerify(publicKey)
signature.update(contenidoCifrado)
val firmaValida = signature.verify(Base64.getDecoder().decode(secreto.firma))

if (firmaValida) {
    // Mostrar contenido: contenidoPlano
    println("Secreto: $contenidoPlano")
} else {
    // Advertir al usuario que la firma no es válida
    println("⚠️ ADVERTENCIA: La firma digital no es válida")
}
```

---

### Flujo 5: Compartir un Secreto

```kotlin
// 1. Obtener el secreto y descifrarlo (igual que Flujo 4)
val secreto = apiService.obtenerSecreto(secretoId = 1)
val claveAES = descifrarClaveAES(secreto.claveAESCifrada) // Usando tu clave privada

// 2. Obtener la clave pública del destinatario
val usuarios = apiService.getUsuariosPublicos()
val destinatario = usuarios.find { it.username == "jane_doe" }!!

val publicKeyDestinatario = Base64.getDecoder().decode(destinatario.publicKey)
val keyFactory = KeyFactory.getInstance("RSA")
val publicKeySpec = X509EncodedKeySpec(publicKeyDestinatario)
val publicKey = keyFactory.generatePublic(publicKeySpec)

// 3. Re-cifrar la clave AES con la clave pública del destinatario
val cipherRSA = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey)
val claveAESCifradaDestinatario = cipherRSA.doFinal(claveAES)

// 4. Enviar al servidor
val request = CompartirSecretoRequest(
    receptorId = destinatario.id,
    claveAESCifradaDestinatario = Base64.getEncoder().encodeToString(claveAESCifradaDestinatario)
)

apiService.compartirSecreto(secretoId = 1, request)
```

---

## 🔒 IMPLEMENTACIÓN CRIPTOGRÁFICA

### Parámetros de Seguridad

```kotlin
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

// PBKDF2
const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
const val PBKDF2_ITERATIONS = 100000
const val PBKDF2_KEY_LENGTH = 256

// Firma digital
const val SIGNATURE_ALGORITHM = "SHA256withRSA"
```

---

## ✅ RESUMEN DE RESPONSABILIDADES

### ❌ Backend (Zero-Knowledge)
- ❌ NO genera claves privadas
- ❌ NO descifra datos
- ❌ NO conoce contraseñas de cifrado
- ✅ Almacena datos cifrados
- ✅ Firma claves públicas
- ✅ Valida autenticación

### ✅ Cliente Android
- ✅ Genera par de claves RSA
- ✅ Cifra/descifra su clave privada
- ✅ Cifra contenido con AES
- ✅ Firma datos
- ✅ Verifica firmas
- ✅ Gestiona todo el cifrado E2E

---

## 🎓 NOTAS IMPORTANTES

1. **La clave privada NUNCA sale del dispositivo cifrada con la contraseña del usuario**
2. **El servidor NUNCA puede descifrar los secretos**
3. **Verificar SIEMPRE las firmas digitales antes de mostrar contenido**
4. **Validar certificados del servidor cuando sea posible**
5. **Usar SecureRandom para generar IVs y salts**
6. **No reutilizar IVs en AES-GCM**
7. **Implementar PIN/Biometría para proteger el acceso a la clave privada**

---

## 📦 Modelos de Datos Kotlin

```kotlin
// Request/Response para registro
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val nombre: String,
    val rol: String,
    val publicKeyBase64: String // OBLIGATORIO
)

data class RegisterResponse(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val publicKey: String,
    val certificado: String,
    val rol: String
)

// Request/Response para login
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val usuario: UsuarioDTO
)

data class UsuarioDTO(
    val id: Long,
    val username: String,
    val email: String,
    val nombre: String,
    val publicKey: String,
    val certificado: String,
    val rol: String
)

// Usuarios públicos
data class UsuarioPublicoDTO(
    val id: Long,
    val username: String,
    val nombre: String,
    val publicKey: String,
    val certificado: String
)

// Secretos
data class SecretoSummaryDTO(
    val id: Long,
    val autorId: Long,
    val autorUsername: String,
    val autorNombre: String,
    val esAutor: Boolean,
    val cantidadCompartidos: Int
)

data class CrearSecretoRequest(
    val contenidoCifrado: String, // Base64
    val claveAESCifrada: String,  // Base64
    val firma: String,            // Base64
    val iv: String                // Base64
)

data class SecretoCifradoResponse(
    val id: Long,
    val autorId: Long,
    val autorUsername: String,
    val autorNombre: String,
    val contenidoCifrado: String,  // Base64
    val claveAESCifrada: String,   // Base64
    val firma: String,             // Base64
    val iv: String,                // Base64
    val publicKeyAutor: String,    // Base64
    val esCompartido: Boolean,
    val compartidoCon: List<UsuarioPublicoDTO>
)

data class CompartirSecretoRequest(
    val receptorId: Long,
    val claveAESCifradaDestinatario: String // Base64
)
```

---

## 🚀 ¡Listo para Implementar!

Con esta guía tienes todo lo necesario para implementar un cliente Android completamente seguro que:

✅ Gestiona claves RSA de 4096 bits  
✅ Cifra datos con AES-256-GCM  
✅ Implementa firmas digitales SHA256withRSA  
✅ Protege claves privadas con PBKDF2  
✅ Almacena datos sensibles cifrados en DataStore  
✅ Implementa Zero-Knowledge con el servidor  

**El servidor está 100% listo y esperando tu cliente Android. ¡A programar! 🎉**

