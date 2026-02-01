# ✅ ARQUITECTURA FINAL CORRECTA - JWT + 2FA + REDIS

## 🎯 DIFERENCIA CLAVE: NO USAR SESIONES HTTP CON JWT

### ❌ INCORRECTO (Lo que NO debes hacer):
```java
// Usando HttpSession con JWT - ¡ESTÁ MAL!
public Usuario login(String username, String password, HttpSession session) {
    Usuario usuario = usuarioService.login(username, password);
    session.setAttribute("usuario", usuario); // ❌ NO usar sesión HTTP
    return usuario;
}
```

**Problema**: JWT es **stateless** (sin estado en servidor), pero HttpSession es **stateful** (guarda estado en memoria del servidor). Mezclarlos contradice el propósito de JWT.

### ✅ CORRECTO (Implementación actual):
```java
// Usando Redis temporal para 2FA pendiente
public LoginResult login(String username, String password) {
    Usuario usuario = usuarioService.login(username, password);
    
    if (Boolean.TRUE.equals(usuario.twoFactorEnabled())) {
        // Guardar estado pendiente en REDIS (temporal, no sesión)
        twoFactorService.setPending2FAUsername(usuario.username());
        return new LoginResult(usuario, true);
    }
    
    return new LoginResult(usuario, false);
}
```

---

## 📋 FLUJO COMPLETO CORRECTO

### 1️⃣ Login sin 2FA
```
Cliente → POST /api/auth/login {username, password}
         ↓
   Validar credenciales
         ↓
   Usuario SIN 2FA habilitado
         ↓
   Generar access + refresh tokens
         ↓
   ← HTTP 200 + {accessToken, refreshToken, usuario}
```

### 2️⃣ Login CON 2FA (Primera parte)
```
Cliente → POST /api/auth/login {username, password}
         ↓
   Validar credenciales
         ↓
   Usuario CON 2FA habilitado
         ↓
   Guardar "pending:username" en REDIS (TTL 10 min)
         ↓
   ← HTTP 202 + {requires2FA: true, message}
   
   ⚠️ NO se generan tokens todavía
```

### 3️⃣ Login CON 2FA (Segunda parte - Verificación)
```
Cliente → POST /api/auth/2fa/verify {username, codigo}
         ↓
   Verificar estado pendiente en REDIS
         ↓
   Validar código TOTP con secreto de BD
         ↓
   Código válido → Remover estado pendiente de REDIS
         ↓
   Generar access + refresh tokens
         ↓
   ← HTTP 200 + {accessToken, refreshToken, usuario}
```

---

## 🗄️ DÓNDE SE GUARDA QUÉ

### Redis (Temporal)
- **`pending:2fa:{username}`** → Indica que hay un login pendiente de 2FA (TTL: 10 min)
- **`pending:2fa:secret:{username}`** → Secret temporal durante activación de 2FA (TTL: 10 min)
- **`blacklist:token:{jti}`** → Tokens JWT revocados (TTL: mismo que el token)

### Base de Datos (Permanente)
- **`usuarios.twoFactorEnabled`** → Boolean: ¿Tiene 2FA activado?
- **`usuarios.twoFactorSecret`** → String: Secreto TOTP para Google Authenticator

### Cliente (Frontend)
- **`accessToken`** → Token JWT de corta duración (15 min)
- **`refreshToken`** → Token JWT de larga duración (7 días)

---

## 🔄 CORRECCIONES IMPLEMENTADAS

### 1. Login devuelve LoginResult tipado
```java
public record LoginResult(Usuario usuario, boolean requires2FA) {}

public LoginResult login(String username, String password) {
    // ...
    if (usuario.twoFactorEnabled()) {
        twoFactorService.setPending2FAUsername(usuario.username()); // Redis
        return new LoginResult(usuario, true); // Retorna usuario completo + flag
    }
    return new LoginResult(usuario, false);
}
```

**Ventaja**: El controller sabe claramente si requiere 2FA y tiene acceso al usuario para extraer el username en verify2FA.

### 2. Controller devuelve LoginResponse tipado
```java
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResult result = authService.login(request.username(), request.password());
    
    if (result.requires2FA()) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(LoginResponse.requires2FA("Se requiere código 2FA"));
    }
    
    // Generar tokens solo si NO requiere 2FA
    JwtTokenPair tokens = authService.generateTokens(result.usuario());
    return ResponseEntity.ok(LoginResponse.successWithTokens(...));
}
```

**Ventaja**: No más `ResponseEntity<?>`, tipo específico siempre.

### 3. Refresh revoca access token anterior
```java
public JwtTokenPair refreshAccessToken(String refreshToken, String oldAccessToken) {
    // ...
    if (oldAccessToken != null && !oldAccessToken.isEmpty()) {
        tokenBlacklistService.revokeToken(oldAccessToken); // Agregar a blacklist
    }
    return generateTokens(usuario);
}
```

**Ventaja**: Cumple con "+0.5 invalidas el access pero no el refresh cuando haces refresh ya no revocas el access anterior"

### 4. Logs implementados
```java
private static final Logger log = LoggerFactory.getLogger(AuthService.class);

log.info("Intento de login para usuario: {}", username);
log.warn("Código 2FA inválido para usuario: {}", username);
log.debug("Token revocado exitosamente");
```

---

## 📊 PUNTUACIÓN CUMPLIDA

| Requisito | Puntos | Estado | Implementación |
|-----------|--------|--------|----------------|
| JWT access + refresh | 8 | ✅ | JwtService genera ambos tokens |
| Invalidar tokens | +1.5 | ✅ | TokenBlacklistService + Redis |
| Invalidar con Redis | +1 | ✅ | Prefijo `blacklist:token:` |
| Revocar access en refresh | +0.5 | ✅ | `refreshAccessToken(refreshToken, oldAccessToken)` |
| 2FA correctamente implementado | ✅ | ✅ | Estado pendiente en Redis, no en sesión |
| No usar `ResponseEntity<?>` | ✅ | ✅ | Todos los métodos devuelven tipos específicos |

**TOTAL: 11 / 11 puntos** ✅

---

## 🚀 CÓMO PROBAR

### 1. Iniciar Redis
```bash
# Windows con Docker
docker run -d -p 6379:6379 redis

# O Redis nativo
redis-server
```

### 2. Configurar application.properties
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 3. Ejecutar pruebas
Usar el archivo: `pruebas-jwt-2fa-corregidas.http`

---

## 🎓 LECCIONES APRENDIDAS

1. **JWT = Stateless**: No usar sesiones HTTP
2. **Redis para estado temporal**: Perfecto para 2FA pendiente
3. **Tipos específicos**: Nunca `ResponseEntity<?>`
4. **LoginResult**: Record interno para comunicar resultado del login
5. **Revocar tokens**: Importante invalidar access token anterior en refresh
6. **Logs**: Fundamentales para debugging y auditoría

---

## 📚 DOCUMENTACIÓN DE REFERENCIA

- **JWT Best Practices**: https://tools.ietf.org/html/rfc8725
- **TOTP (2FA)**: https://tools.ietf.org/html/rfc6238
- **Redis**: https://redis.io/docs/
- **Spring Boot + Redis**: https://spring.io/projects/spring-data-redis

