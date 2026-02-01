# 📚 Documentación Completa: Sistema de Autenticación de Dos Factores (2FA)

## 🎯 Tabla de Contenidos
1. [Introducción](#introducción)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Modelos de Datos](#modelos-de-datos)
4. [Flujo de Autenticación 2FA](#flujo-de-autenticación-2fa)
5. [Implementación Detallada](#implementación-detallada)
6. [Dependencias y Configuración](#dependencias-y-configuración)
7. [Casos de Uso Completos](#casos-de-uso-completos)
8. [Guía de Implementación](#guía-de-implementación)

---

## 📖 Introducción

Este sistema implementa **Autenticación de Dos Factores (2FA)** en una aplicación Spring Boot, soportando dos métodos diferentes:

1. **TOTP (Time-based One-Time Password)**: Genera códigos temporales usando aplicaciones como Google Authenticator
2. **EMAIL**: Envía códigos de verificación por correo electrónico

### ¿Qué es 2FA?

2FA añade una capa adicional de seguridad al proceso de autenticación. Incluso si alguien obtiene tu contraseña, necesitará el segundo factor (código temporal o enviado por email) para acceder a la cuenta.

---

## 🏗️ Arquitectura del Sistema

### Estructura de Capas

```
┌─────────────────────────────────────────┐
│        UI Layer (Controllers)           │
│  - AuthController                       │
│  - TwoFactorAuthController              │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Service Layer                    │
│  - AuthService (Lógica 2FA)             │
│  - TotpService (Generación códigos)     │
│  - EmailService (Envío emails)          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Domain Layer                     │
│  - UsuarioService                       │
│  - Usuario (Modelo)                     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Data Layer                       │
│  - UsuarioRepository                    │
│  - UsuarioEntity                        │
└─────────────────────────────────────────┘
```

---

## 💾 Modelos de Datos

### 1. Enum TwoFactorMethod

Define los métodos de 2FA disponibles:

```java
public enum TwoFactorMethod {
    TOTP,    // Time-based One-Time Password (Google Authenticator)
    EMAIL,   // Código enviado por correo electrónico
    NONE     // Sin 2FA
}
```

### 2. Usuario (Record)

```java
public record Usuario(
    Long id,
    String username,
    String password,
    String email,
    String nombre,
    boolean activado,
    String codigoActivacion,
    LocalDateTime fechaExpiracionCodigo,  // Expiración del código
    Rol rol,
    Boolean twoFactorEnabled,             // ¿2FA está activo?
    TwoFactorMethod twoFactorMethod,      // Método de 2FA
    String twoFactorSecret                // Secreto TOTP o código EMAIL
)
```

**Campos importantes para 2FA:**
- **twoFactorEnabled**: `true` si el usuario ya confirmó y activó 2FA
- **twoFactorMethod**: Tipo de 2FA (TOTP, EMAIL o NONE)
- **twoFactorSecret**: 
  - Para TOTP: El secreto compartido entre servidor y app del usuario
  - Para EMAIL: El código de 6 dígitos enviado por correo
- **fechaExpiracionCodigo**: Fecha de expiración del código (10 minutos para EMAIL)

### 3. UsuarioEntity (Base de Datos)

```java
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String email;
    private String nombre;
    private boolean activado;
    private String codigoActivacion;
    private LocalDateTime fechaExpiracionCodigo;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
    private Boolean twoFactorEnabled;
    
    @Enumerated(EnumType.STRING)
    private TwoFactorMethod twoFactorMethod;
    
    private String twoFactorSecret;
    
    // getters y setters...
}
```

### 4. Constantes de Sesión

```java
public static final String SESSION_ATTR_USUARIO = "usuario";
public static final String SESSION_ATTR_2FA_PENDING_USER_ID = "2fa_pending_user_id";
```

**Importante**: Durante el proceso 2FA, NO se guarda el usuario completo en sesión, solo su ID. Esto es por seguridad.

---

## 🔄 Flujo de Autenticación 2FA

### Flujo 1: Activar 2FA en una cuenta (TOTP)

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│ Cliente  │         │ Servidor │         │   BD     │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                     │
     │  1. Login normal   │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │  2. Usuario en     │                     │
     │     sesión         │                     │
     │◄───────────────────┤                     │
     │                    │                     │
     │  3. POST           │                     │
     │  /2fa/enable       │                     │
     │  ?method=TOTP      │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  4. Generar secreto │
     │                    │     aleatorio       │
     │                    │     (Base32)        │
     │                    │                     │
     │                    │  5. Generar QR code │
     │                    │     (imagen Base64) │
     │                    │                     │
     │                    │  6. Guardar:        │
     │                    │  - twoFactorEnabled=FALSE
     │                    │  - method=TOTP      │
     │                    │  - secret           │
     │                    ├────────────────────►│
     │                    │                     │
     │  7. Retornar:      │                     │
     │  - secret          │                     │
     │  - qrCodeUrl       │                     │
     │◄───────────────────┤                     │
     │                    │                     │
     │  8. Escanear QR    │                     │
     │     con Google     │                     │
     │     Authenticator  │                     │
     │                    │                     │
     │  9. POST           │                     │
     │  /2fa/confirm      │                     │
     │  {"code":"123456"} │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  10. Verificar      │
     │                    │      código TOTP    │
     │                    │                     │
     │                    │  11. Actualizar:    │
     │                    │  twoFactorEnabled=TRUE
     │                    ├────────────────────►│
     │                    │                     │
     │  12. Confirmación  │                     │
     │◄───────────────────┤                     │
     │                    │                     │
```

### Flujo 2: Activar 2FA en una cuenta (EMAIL)

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│ Cliente  │         │ Servidor │         │   BD     │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                     │
     │  1. POST           │                     │
     │  /2fa/enable       │                     │
     │  ?method=EMAIL     │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  2. Generar código  │
     │                    │     6 dígitos       │
     │                    │     (100000-999999) │
     │                    │                     │
     │                    │  3. Enviar email    │
     │                    │     con código      │
     │                    │                     │
     │                    │  4. Guardar:        │
     │                    │  - twoFactorEnabled=FALSE
     │                    │  - method=EMAIL     │
     │                    │  - secret=código    │
     │                    │  - expira en 10min  │
     │                    ├────────────────────►│
     │                    │                     │
     │  5. Mensaje:       │                     │
     │  "Email enviado"   │                     │
     │◄───────────────────┤                     │
     │                    │                     │
     │  6. Revisar email  │                     │
     │                    │                     │
     │  7. POST           │                     │
     │  /2fa/confirm      │                     │
     │  {"code":"660153"} │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  8. Verificar:      │
     │                    │  - código correcto  │
     │                    │  - no expirado      │
     │                    │                     │
     │                    │  9. Actualizar:     │
     │                    │  twoFactorEnabled=TRUE
     │                    ├────────────────────►│
     │                    │                     │
     │  10. Confirmación  │                     │
     │◄───────────────────┤                     │
     │                    │                     │
```

### Flujo 3: Login con 2FA (TOTP)

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│ Cliente  │         │ Servidor │         │   BD     │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                     │
     │  1. POST /login    │                     │
     │  {username,        │                     │
     │   password}        │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  2. Validar         │
     │                    │     credenciales    │
     │                    ├────────────────────►│
     │                    │                     │
     │                    │  3. Usuario tiene   │
     │                    │     2FA=true        │
     │                    │     method=TOTP     │
     │                    │◄────────────────────┤
     │                    │                     │
     │                    │  4. Guardar en      │
     │                    │     sesión solo:    │
     │                    │  "2fa_pending_      │
     │                    │   user_id" = 123    │
     │                    │                     │
     │  5. HTTP 202       │                     │
     │  "Se requiere 2FA" │                     │
     │◄───────────────────┤                     │
     │                    │                     │
     │  6. Abrir Google   │                     │
     │     Authenticator  │                     │
     │     ver código     │                     │
     │                    │                     │
     │  7. POST           │                     │
     │  /2fa/verify       │                     │
     │  {"code":"123456"} │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  8. Obtener ID      │
     │                    │     pendiente de    │
     │                    │     sesión          │
     │                    │                     │
     │                    │  9. Buscar usuario  │
     │                    │     y su secret     │
     │                    ├────────────────────►│
     │                    │◄────────────────────┤
     │                    │                     │
     │                    │  10. Verificar      │
     │                    │      código TOTP    │
     │                    │                     │
     │                    │  11. Remover        │
     │                    │   "2fa_pending_id"  │
     │                    │   Agregar "usuario" │
     │                    │   a sesión          │
     │                    │                     │
     │  12. HTTP 200      │                     │
     │  Usuario completo  │                     │
     │  + token sesión    │                     │
     │◄───────────────────┤                     │
     │                    │                     │
     │  13. Acceso total  │                     │
     │      a la app      │                     │
     │                    │                     │
```

### Flujo 4: Login con 2FA (EMAIL)

```
┌──────────┐         ┌──────────┐         ┌──────────┐
│ Cliente  │         │ Servidor │         │   BD     │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                     │
     │  1. POST /login    │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  2. Validar         │
     │                    │     credenciales    │
     │                    ├────────────────────►│
     │                    │                     │
     │                    │  3. Usuario tiene   │
     │                    │     2FA=true        │
     │                    │     method=EMAIL    │
     │                    │◄────────────────────┤
     │                    │                     │
     │                    │  4. Generar nuevo   │
     │                    │     código 6 dígitos│
     │                    │                     │
     │                    │  5. Enviar email    │
     │                    │                     │
     │                    │  6. Actualizar:     │
     │                    │  - secret=código    │
     │                    │  - expira en 10min  │
     │                    ├────────────────────►│
     │                    │                     │
     │                    │  7. Guardar en      │
     │                    │     sesión:         │
     │                    │  "2fa_pending_      │
     │                    │   user_id"          │
     │                    │                     │
     │  8. HTTP 202       │                     │
     │  "Se requiere 2FA" │                     │
     │◄───────────────────┤                     │
     │                    │                     │
     │  9. Revisar email  │                     │
     │     (código válido │                     │
     │      10 minutos)   │                     │
     │                    │                     │
     │  10. POST          │                     │
     │  /2fa/verify       │                     │
     │  {"code":"224677"} │                     │
     ├───────────────────►│                     │
     │                    │                     │
     │                    │  11. Buscar usuario │
     │                    ├────────────────────►│
     │                    │◄────────────────────┤
     │                    │                     │
     │                    │  12. Verificar:     │
     │                    │   - código==secret  │
     │                    │   - no expirado     │
     │                    │                     │
     │                    │  13. Autenticar     │
     │                    │      usuario        │
     │                    │                     │
     │  14. HTTP 200      │                     │
     │  Login exitoso     │                     │
     │◄───────────────────┤                     │
     │                    │                     │
```

---

## 🔧 Implementación Detallada

### 1. TotpService - Generación y Verificación TOTP

```java
@Service
public class TotpService {

    // Genera un secreto aleatorio en Base32
    public String generateSecret() {
        return Base32.random();
        // Ejemplo: "JBSWY3DPEHPK3PXP"
    }

    // Genera el QR code como imagen Base64
    public String generateQrCodeImageUri(String secret, String username, String issuer) 
            throws WriterException, IOException {
        
        // Formato estándar otpauth://
        String qrCodeText = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, username, secret, issuer);
        // Ejemplo: "otpauth://totp/API Renos:juan?secret=JBSWY3DPEHPK3PXP&issuer=API Renos"
        
        // Generar QR code de 250x250 píxeles
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 250, 250);
        
        // Convertir a imagen PNG en Base64
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] imageData = pngOutputStream.toByteArray();
        
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        return "data:image/png;base64," + base64Image;
    }

    // Verifica si el código ingresado es válido
    public boolean verifyCode(String secret, String code) {
        try {
            Totp totp = new Totp(secret);
            return totp.verify(code);
            // Los códigos TOTP cambian cada 30 segundos
            // La librería permite una ventana de tolerancia
        } catch (Exception _) {
            return false;
        }
    }
}
```

**¿Cómo funciona TOTP?**

1. Se genera un **secreto compartido** (Base32) que solo conocen el servidor y el usuario
2. Tanto el servidor como la app del usuario (Google Authenticator) usan el mismo algoritmo
3. El algoritmo combina:
   - El secreto compartido
   - El tiempo actual (en intervalos de 30 segundos)
4. Genera un código de 6 dígitos que cambia cada 30 segundos
5. El servidor y la app generan el MISMO código al mismo tiempo

### 2. EmailService - Envío de Códigos por Email

```java
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // Envía código 2FA por email
    public void enviarCodigo2FA(String usuario, String email, String codigo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Código de verificación 2FA");

            // Usar plantilla Thymeleaf con variables
            Context context = new Context();
            context.setVariable("usuario", usuario);
            context.setVariable("codigo2FA", codigo);

            String htmlContent = templateEngine.process("email-2fa", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException _) {
            throw new EmailException("Error al enviar el email de 2FA");
        }
    }
}
```

**Plantilla email-2fa.html:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Código de Verificación 2FA</title>
</head>
<body>
    <h1>Hola <span th:text="${usuario}"></span>!</h1>
    <p>Tu código de verificación 2FA es:</p>
    <h2 style="font-size: 32px; color: #2196F3;" th:text="${codigo2FA}"></h2>
    <p>Este código expira en 10 minutos.</p>
</body>
</html>
```

### 3. AuthService - Lógica Principal de 2FA

#### 3.1 Método: enable2FA

```java
public ResponseEntity<Enable2FAResponse> enable2FA(String method, HttpSession session) {
    // Obtener usuario de la sesión (debe estar autenticado)
    Usuario usuario = (Usuario) session.getAttribute(Constantes.SESSION_ATTR_USUARIO);

    // Validar que no tenga 2FA ya activado
    if (Boolean.TRUE.equals(usuario.twoFactorEnabled())) {
        Enable2FAResponse response = new Enable2FAResponse("Ya tienes 2FA activado");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // CASO 1: Método TOTP
    if (TwoFactorMethod.TOTP.name().equalsIgnoreCase(method)) {
        try {
            // 1. Generar secreto aleatorio
            String secret = totpService.generateSecret();
            
            // 2. Generar QR code con el secreto
            String qrCodeUrl = totpService.generateQrCodeImageUri(
                secret, 
                usuario.username(), 
                "API Renos"
            );

            // 3. Guardar configuración 2FA (PENDIENTE de confirmación)
            Usuario updatedUsuario = usuario.set2FA(
                false,              // twoFactorEnabled = FALSE (aún no confirmado)
                TwoFactorMethod.TOTP,
                secret
            );
            usuarioService.update2FA(updatedUsuario);

            // 4. Retornar secreto y QR al cliente
            Enable2FAResponse response = new Enable2FAResponse(
                secret, 
                qrCodeUrl, 
                "Escanea el código QR con tu app"
            );
            return ResponseEntity.ok(response);
            
        } catch (WriterException | IOException _) {
            Enable2FAResponse response = new Enable2FAResponse("Error generando QR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // CASO 2: Método EMAIL
    else if (TwoFactorMethod.EMAIL.name().equalsIgnoreCase(method)) {
        // Genera código y envía email
        send2FACodeByEmail(usuario);
        
        Enable2FAResponse response = new Enable2FAResponse(
            "Se ha enviado un código a tu email"
        );
        return ResponseEntity.ok(response);
    }
    
    // CASO 3: Método no soportado
    else {
        Enable2FAResponse response = new Enable2FAResponse(
            "Método no soportado. Usa 'TOTP' o 'EMAIL'"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
```

#### 3.2 Método: confirm2FA

```java
public ResponseEntity<Confirm2FAResponse> confirm2FA(String code, HttpSession session) {
    // 1. Obtener usuario de la sesión
    Usuario usuario = usuarioService.findById(getUsuarioIdFromSession(session));

    // 2. Validar que hay un proceso de activación en curso
    if (usuario.twoFactorMethod() == null) {
        Confirm2FAResponse response = new Confirm2FAResponse(
            false, 
            "No hay ningún proceso de 2FA pendiente"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    boolean isValid = false;

    // 3. VERIFICAR según el método
    
    // CASO 1: TOTP
    if (usuario.twoFactorMethod() == TwoFactorMethod.TOTP) {
        // Verificar el código con el secreto guardado
        isValid = totpService.verifyCode(usuario.twoFactorSecret(), code);
    }
    
    // CASO 2: EMAIL
    else if (usuario.twoFactorMethod() == TwoFactorMethod.EMAIL) {
        // Verificar que no haya expirado
        if (usuario.fechaExpiracionCodigo() == null ||
            usuario.fechaExpiracionCodigo().isBefore(LocalDateTime.now())) {
            Confirm2FAResponse response = new Confirm2FAResponse(
                false, 
                "El código ha expirado. Solicita uno nuevo."
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        // Comparar código ingresado con el guardado
        isValid = usuario.twoFactorSecret().equals(code);
    }

    // 4. Si el código es válido, ACTIVAR 2FA definitivamente
    if (isValid) {
        Usuario updatedUsuario = usuario.set2FA(
            true,                           // ¡AHORA SÍ activado!
            usuario.twoFactorMethod(),
            usuario.twoFactorSecret()
        );
        usuarioService.update2FA(updatedUsuario);

        Confirm2FAResponse response = new Confirm2FAResponse(
            true, 
            "2FA activado exitosamente"
        );
        return ResponseEntity.ok(response);
    }

    // 5. Código inválido
    Confirm2FAResponse response = new Confirm2FAResponse(
        false, 
        "Código inválido"
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
}
```

#### 3.3 Método: login (con soporte 2FA)

```java
public Usuario login(String username, String password, HttpSession session) {
    // 1. Validar credenciales (username + password)
    Usuario usuario = usuarioService.login(username, password);

    // 2. Decidir flujo según estado de 2FA
    
    // CASO 1: Usuario SIN 2FA
    if (Boolean.FALSE.equals(usuario.twoFactorEnabled())) {
        // Login completo, guardar usuario en sesión
        session.setAttribute(Constantes.SESSION_ATTR_USUARIO, usuario);
    }
    
    // CASO 2: Usuario CON 2FA
    else {
        // Si usa EMAIL, enviar código automáticamente
        if (usuario.twoFactorMethod() == TwoFactorMethod.EMAIL) {
            send2FACodeByEmail(usuario);
        }
        
        // NO guardar usuario completo, solo su ID
        // Esto es CRÍTICO para seguridad
        session.setAttribute(
            Constantes.SESSION_ATTR_2FA_PENDING_USER_ID, 
            usuario.id()
        );
    }
    
    return usuario;
}
```

#### 3.4 Método: verify2FA

```java
public ResponseEntity<LoginResponse> verify2FA(String code, HttpSession session) {
    // 1. Obtener ID del usuario pendiente de 2FA
    Long pendingUserId = getPendingUsuarioIdFromSession(session);

    if (pendingUserId == null) {
        LoginResponse response = new LoginResponse("No hay proceso de 2FA pendiente");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 2. Buscar usuario completo
    Usuario pendingUser = usuarioService.findById(pendingUserId);

    // 3. Validar que tiene 2FA activado
    if (Boolean.FALSE.equals(pendingUser.twoFactorEnabled())) {
        LoginResponse response = new LoginResponse("No hay proceso de 2FA pendiente");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    boolean isValid = false;

    // 4. VERIFICAR código según el método
    
    // CASO 1: TOTP
    if (pendingUser.twoFactorMethod() == TwoFactorMethod.TOTP) {
        isValid = totpService.verifyCode(pendingUser.twoFactorSecret(), code);
    }
    
    // CASO 2: EMAIL
    else if (pendingUser.twoFactorMethod() == TwoFactorMethod.EMAIL) {
        // Verificar expiración
        if (pendingUser.fechaExpiracionCodigo() == null ||
            pendingUser.fechaExpiracionCodigo().isBefore(LocalDateTime.now())) {
            LoginResponse response = new LoginResponse("Código expirado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        // Comparar código
        isValid = pendingUser.twoFactorSecret().equals(code);
    }

    // 5. Si es válido, COMPLETAR LOGIN
    if (isValid) {
        // Limpiar ID pendiente
        session.removeAttribute(Constantes.SESSION_ATTR_2FA_PENDING_USER_ID);
        
        // AHORA SÍ guardar usuario completo en sesión
        session.setAttribute(Constantes.SESSION_ATTR_USUARIO, pendingUser);

        UsuarioDTO usuarioDTO = new UsuarioDTO(
            pendingUser.id(),
            pendingUser.username(),
            pendingUser.email(),
            pendingUser.nombre(),
            pendingUser.rol()
        );

        LoginResponse response = new LoginResponse(usuarioDTO, "Login exitoso");
        return ResponseEntity.ok(response);
    }

    // 6. Código inválido
    LoginResponse response = new LoginResponse("Código inválido");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
}
```

#### 3.5 Método: send2FACodeByEmail (privado)

```java
private void send2FACodeByEmail(Usuario usuario) {
    // 1. Generar código de 6 dígitos aleatorio
    String code = String.valueOf(random.nextInt(100000, 999999));
    // Ejemplo: "224677"

    // 2. Enviar email con el código
    emailService.enviarCodigo2FA(usuario.username(), usuario.email(), code);

    // 3. Actualizar usuario con código y expiración
    Usuario updatedUsuario = new Usuario(
        usuario.id(),
        usuario.username(),
        usuario.password(),
        usuario.email(),
        usuario.nombre(),
        usuario.activado(),
        usuario.codigoActivacion(),
        LocalDateTime.now().plusMinutes(10),  // Expira en 10 minutos
        usuario.rol(),
        usuario.twoFactorEnabled(),
        TwoFactorMethod.EMAIL,
        code  // Guardar código en el campo twoFactorSecret
    );

    // 4. Persistir en base de datos
    usuarioService.update2FA(updatedUsuario);
}
```

### 4. Controllers

#### 4.1 TwoFactorAuthController

```java
@RestController
public class TwoFactorAuthController {
    private final AuthService authService;

    // POST /2fa/enable?method=TOTP
    // POST /2fa/enable?method=EMAIL
    @RequiresAuth  // Requiere estar autenticado
    @PostMapping("/2fa/enable")
    public ResponseEntity<Enable2FAResponse> enable2FA(
            @RequestParam String method, 
            HttpSession session) {
        return authService.enable2FA(method, session);
    }

    // POST /2fa/confirm
    // Body: {"code": "123456"}
    @RequiresAuth
    @PostMapping("/2fa/confirm")
    public ResponseEntity<Confirm2FAResponse> confirm2FA(
            @RequestBody Confirm2FADTO request, 
            HttpSession session) {
        return authService.confirm2FA(request.code(), session);
    }

    // POST /2fa/verify
    // Body: {"code": "123456"}
    @PostMapping("/2fa/verify")
    public ResponseEntity<LoginResponse> verify2FA(
            @RequestBody Verify2FADTO request, 
            HttpSession session) {
        return authService.verify2FA(request.code(), session);
    }
}
```

#### 4.2 AuthController

```java
@RestController
public class AuthController {
    private final AuthService authService;
    private final UsuarioService usuarioService;

    // POST /login
    // Body: {"username": "juan", "password": "juan123"}
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginDTO request, 
            HttpSession session) {
        
        Usuario usuario = authService.login(
            request.username(), 
            request.password(), 
            session
        );

        // Si tiene 2FA activado, retornar HTTP 202 (Accepted)
        if (Boolean.TRUE.equals(usuario.twoFactorEnabled())) {
            LoginResponse response = new LoginResponse("Se requiere verificación 2FA");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }

        // Login completo sin 2FA
        UsuarioDTO usuarioDTO = new UsuarioDTO(
            usuario.id(),
            usuario.username(),
            usuario.email(),
            usuario.nombre(),
            usuario.rol()
        );

        LoginResponse response = new LoginResponse(usuarioDTO, "Login exitoso");
        return ResponseEntity.ok(response);
    }

    // POST /logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok("Logout exitoso");
    }

    // POST /register
    // Body: {"username": "nuevo", "password": "pass123", "email": "...", "nombre": "..."}
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterDTO request) {
        if (usuarioService.existsByUsername(request.username())) {
            LoginResponse response = new LoginResponse("El username ya existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Usuario newUser = authService.register(
            request.username(),
            request.password(),
            request.email(),
            request.nombre()
        );

        UsuarioDTO usuarioDTO = new UsuarioDTO(
            newUser.id(),
            newUser.username(),
            newUser.email(),
            newUser.nombre(),
            newUser.rol()
        );

        LoginResponse response = new LoginResponse(
            usuarioDTO, 
            "Registro exitoso. Revisa tu email para activar tu cuenta."
        );
        return ResponseEntity.ok(response);
    }
}
```

### 5. Interceptor de Autenticación

```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Object handler) throws Exception {
        
        // Verificar si el método tiene la anotación @RequiresAuth
        if (handler instanceof HandlerMethod handlerMethod) {
            RequiresAuth requiresAuth = handlerMethod.getMethodAnnotation(RequiresAuth.class);
            
            if (requiresAuth != null) {
                HttpSession session = request.getSession(false);
                
                // Verificar que el usuario esté autenticado
                if (session == null || !authService.isAuthenticated(session)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Debe iniciar sesión");
                    return false;
                }
            }
        }
        
        return true;
    }
}
```

---

## 📦 Dependencias y Configuración

### 1. pom.xml (Maven)

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- H2 Database (desarrollo) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Spring Security Crypto (para hashear passwords) -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-crypto</artifactId>
    </dependency>

    <!-- Spring Boot Mail -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>

    <!-- Thymeleaf (templates de email) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <!-- TOTP (Time-based One-Time Password) -->
    <dependency>
        <groupId>org.jboss.aerogear</groupId>
        <artifactId>aerogear-otp-java</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- Generación de QR Codes -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.5.4</version>
    </dependency>
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>javase</artifactId>
        <version>3.5.4</version>
    </dependency>
</dependencies>
```

### 2. application.properties

```properties
# Nombre de la aplicación
spring.application.name=apilogin

# Base de datos H2 (en memoria, para desarrollo)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always

# Configuración de Email (MailHog para desarrollo)
# Ejecutar: docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

### 3. Configuración de Password Encoder

```java
@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 4. Configuración de Web (Interceptor)

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }
}
```

---

## 🎬 Casos de Uso Completos

### Caso 1: Usuario nuevo activa 2FA con TOTP

**1. Registro:**
```http
POST http://localhost:8080/register
Content-Type: application/json

{
  "username": "juan",
  "password": "juan123",
  "email": "juan@example.com",
  "nombre": "Juan Pérez"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Registro exitoso. Revisa tu email para activar tu cuenta.",
  "usuario": {
    "id": 1,
    "username": "juan",
    "email": "juan@example.com",
    "nombre": "Juan Pérez",
    "rol": "USER"
  }
}
```

**2. Activar cuenta (desde link en email):**
```http
GET http://localhost:8080/activar?codigo=a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**3. Login:**
```http
POST http://localhost:8080/login
Content-Type: application/json

{
  "username": "juan",
  "password": "juan123"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "usuario": {
    "id": 1,
    "username": "juan",
    "email": "juan@example.com",
    "nombre": "Juan Pérez",
    "rol": "USER"
  }
}
```

**4. Habilitar 2FA con TOTP:**
```http
POST http://localhost:8080/2fa/enable?method=TOTP
```

**Respuesta:**
```json
{
  "secret": "JBSWY3DPEHPK3PXP",
  "qrCodeUrl": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "message": "Escanea el código QR con tu aplicación de autenticación 2FA"
}
```

**5. Usuario escanea QR con Google Authenticator**

La app del usuario ahora genera códigos cada 30 segundos.

**6. Confirmar 2FA con código de la app:**
```http
POST http://localhost:8080/2fa/confirm
Content-Type: application/json

{
  "code": "123456"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "2FA ha sido habilitado exitosamente en tu cuenta"
}
```

**7. Logout:**
```http
POST http://localhost:8080/logout
```

**8. Login con 2FA (parte 1 - credenciales):**
```http
POST http://localhost:8080/login
Content-Type: application/json

{
  "username": "juan",
  "password": "juan123"
}
```

**Respuesta (HTTP 202 Accepted):**
```json
{
  "success": false,
  "message": "Se requiere verificación 2FA",
  "usuario": null
}
```

**9. Login con 2FA (parte 2 - código):**
```http
POST http://localhost:8080/2fa/verify
Content-Type: application/json

{
  "code": "654321"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "usuario": {
    "id": 1,
    "username": "juan",
    "email": "juan@example.com",
    "nombre": "Juan Pérez",
    "rol": "USER"
  }
}
```

### Caso 2: Usuario activa 2FA con EMAIL

**1-3. (Igual que caso 1: registro, activación, login)**

**4. Habilitar 2FA con EMAIL:**
```http
POST http://localhost:8080/2fa/enable?method=EMAIL
```

**Respuesta:**
```json
{
  "secret": null,
  "qrCodeUrl": null,
  "message": "Se ha enviado un código de verificación a tu correo electrónico"
}
```

**5. Usuario revisa su email:**
```
Hola maria!
Tu código de verificación 2FA es:
660153
Este código expira en 10 minutos.
```

**6. Confirmar 2FA con código del email:**
```http
POST http://localhost:8080/2fa/confirm
Content-Type: application/json

{
  "code": "660153"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "2FA ha sido habilitado exitosamente en tu cuenta"
}
```

**7. Logout y login posterior:**
```http
POST http://localhost:8080/login
Content-Type: application/json

{
  "username": "maria",
  "password": "maria123"
}
```

**Respuesta (HTTP 202):**
```json
{
  "success": false,
  "message": "Se requiere verificación 2FA",
  "usuario": null
}
```

**IMPORTANTE**: Al hacer login con EMAIL 2FA, el servidor automáticamente:
- Genera un nuevo código de 6 dígitos
- Lo envía al email del usuario
- El código expira en 10 minutos

**8. Usuario revisa nuevo email:**
```
Tu código de verificación 2FA es:
224677
```

**9. Verificar código:**
```http
POST http://localhost:8080/2fa/verify
Content-Type: application/json

{
  "code": "224677"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "usuario": {
    "id": 2,
    "username": "maria",
    "email": "maria@example.com",
    "nombre": "Maria Garcia",
    "rol": "USER"
  }
}
```

---

## 📝 Guía de Implementación

### Paso 1: Preparar la Base de Datos

**Agregar campos a la tabla de usuarios:**

```sql
ALTER TABLE usuarios ADD COLUMN two_factor_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE usuarios ADD COLUMN two_factor_method VARCHAR(10);
ALTER TABLE usuarios ADD COLUMN two_factor_secret VARCHAR(255);
ALTER TABLE usuarios ADD COLUMN fecha_expiracion_codigo TIMESTAMP;
```

### Paso 2: Agregar Dependencias

**Agregar a tu pom.xml:**

```xml
<!-- TOTP -->
<dependency>
    <groupId>org.jboss.aerogear</groupId>
    <artifactId>aerogear-otp-java</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- QR Code -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.4</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.4</version>
</dependency>

<!-- Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Thymeleaf -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

### Paso 3: Crear el Enum TwoFactorMethod

```java
public enum TwoFactorMethod {
    TOTP,
    EMAIL,
    NONE
}
```

### Paso 4: Actualizar tu Modelo Usuario

**Agregar campos:**
```java
private Boolean twoFactorEnabled;
private TwoFactorMethod twoFactorMethod;
private String twoFactorSecret;
private LocalDateTime fechaExpiracionCodigo;
```

### Paso 5: Crear TotpService

**Implementar:**
- `generateSecret()`
- `generateQrCodeImageUri()`
- `verifyCode()`

### Paso 6: Crear EmailService

**Implementar:**
- `enviarCodigo2FA()`

**Crear plantilla `email-2fa.html` en `src/main/resources/templates/`**

### Paso 7: Actualizar AuthService

**Agregar métodos:**
- `enable2FA()`
- `confirm2FA()`
- `verify2FA()`
- `send2FACodeByEmail()`

**Modificar método:**
- `login()` para manejar 2FA

### Paso 8: Crear TwoFactorAuthController

**Endpoints:**
- `POST /2fa/enable`
- `POST /2fa/confirm`
- `POST /2fa/verify`

### Paso 9: Actualizar AuthController

**Modificar:**
- `POST /login` para retornar HTTP 202 cuando requiera 2FA

### Paso 10: Configurar Email

**En application.properties:**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Para desarrollo, usar MailHog:**
```bash
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog
```

```properties
spring.mail.host=localhost
spring.mail.port=1025
```

### Paso 11: Probar

**Para TOTP:**
1. Instalar Google Authenticator en tu móvil
2. Habilitar 2FA con método TOTP
3. Escanear el QR code
4. Usar los códigos generados para confirmar y login

**Para EMAIL:**
1. Configurar servidor de email
2. Habilitar 2FA con método EMAIL
3. Revisar email para códigos
4. Usar códigos para confirmar y login

---

## 🔒 Consideraciones de Seguridad

### 1. **NO guardar el usuario completo durante 2FA pendiente**

❌ **MAL:**
```java
session.setAttribute("usuario", usuario);
```

✅ **BIEN:**
```java
session.setAttribute("2fa_pending_user_id", usuario.id());
```

**Razón**: Si guardas el usuario completo, un atacante con acceso a la sesión podría saltarse el 2FA.

### 2. **Códigos EMAIL deben expirar**

```java
LocalDateTime.now().plusMinutes(10)  // 10 minutos
```

### 3. **Secretos TOTP deben ser únicos por usuario**

```java
String secret = Base32.random();  // Genera nuevo secreto cada vez
```

### 4. **Hashear passwords**

```java
passwordEncoder.encode(password);  // BCrypt
```

### 5. **Validar que el usuario está autenticado antes de enable2FA**

```java
@RequiresAuth
@PostMapping("/2fa/enable")
public ResponseEntity<Enable2FAResponse> enable2FA(...)
```

### 6. **HTTPS en producción**

Nunca uses HTTP en producción, especialmente con 2FA.

### 7. **Rate limiting**

Implementa límite de intentos para prevenir fuerza bruta en códigos 2FA.

### 8. **Backup codes**

En producción, considera generar códigos de respaldo para cuando el usuario pierda acceso a su 2FA.

---

## 📊 Comparación TOTP vs EMAIL

| Característica | TOTP | EMAIL |
|----------------|------|-------|
| **Seguridad** | ⭐⭐⭐⭐⭐ Muy alta | ⭐⭐⭐ Media-Alta |
| **Facilidad de uso** | ⭐⭐⭐ Requiere app | ⭐⭐⭐⭐⭐ Solo email |
| **Dependencia** | Sin internet después de configurar | Requiere acceso a email |
| **Expiración** | Códigos cada 30 seg | Códigos expiran en 10 min |
| **Costo** | Gratis | Costo de email (si alto volumen) |
| **Recuperación** | Difícil si pierdes dispositivo | Fácil (solo email) |
| **Recomendado para** | Usuarios técnicos | Usuarios generales |

---

## 🎓 Conclusión

Este sistema implementa una autenticación de dos factores robusta con dos métodos diferentes:

1. **TOTP**: Ideal para máxima seguridad, usa algoritmos criptográficos y códigos que cambian cada 30 segundos
2. **EMAIL**: Más accesible, pero depende del email del usuario

**Conceptos clave que aprendiste:**

- ✅ Separar el flujo de login en dos pasos cuando 2FA está activado
- ✅ NO guardar información sensible en sesión durante el proceso 2FA
- ✅ Generar y validar códigos TOTP usando secretos compartidos
- ✅ Generar QR codes para facilitar configuración de TOTP
- ✅ Enviar códigos por email con expiración
- ✅ Validar códigos y manejar expiraciones
- ✅ Diferencia entre "activar 2FA" y "login con 2FA"

**Para implementar en tu proyecto:**

1. Copia la estructura de servicios (TotpService, EmailService, AuthService)
2. Agrega los campos de 2FA a tu modelo de Usuario
3. Implementa los endpoints de 2FA
4. Configura el envío de emails
5. Prueba ambos flujos completos

¡Ahora tienes un conocimiento completo de cómo funciona 2FA y puedes implementarlo en cualquier aplicación!

