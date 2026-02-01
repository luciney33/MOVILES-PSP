# 📚 APUNTES COMPLETOS - SPRING SECURITY CON ANOTACIONES

## 📋 Índice
1. [Introducción](#introducción)
2. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
3. [Configuración de Spring Security](#configuración-de-spring-security)
4. [Autenticación JWT](#autenticación-jwt)
5. [Seguridad mediante Anotaciones](#seguridad-mediante-anotaciones)
6. [Anotaciones Personalizadas](#anotaciones-personalizadas)
7. [Clases Clave del Proyecto](#clases-clave-del-proyecto)
8. [Casos de Uso y Ejemplos](#casos-de-uso-y-ejemplos)

---

## 🎯 Introducción

Este proyecto demuestra la implementación completa de **Spring Security** utilizando:
- **Autenticación JWT (JSON Web Tokens)** para APIs REST
- **Seguridad basada en anotaciones** (@PreAuthorize, @Secured, @RolesAllowed)
- **Anotaciones personalizadas** para simplificar reglas de seguridad
- **Múltiples SecurityFilterChains** (API REST + Web tradicional)
- **Integración con JPA** y base de datos H2

### Tecnologías Utilizadas
- **Spring Boot 4.0.1**
- **Spring Security 6.x**
- **JWT (jjwt 0.11.5)**
- **Spring Data JPA**
- **H2 Database** (base de datos en memoria)
- **Lombok**

---

## 🏗️ Arquitectura del Proyecto

### Estructura de Paquetes

```
org.example.springsecurity/
├── config/                          # Configuración de Spring Security
│   ├── SecurityConfig.java         # Configuración de SecurityFilterChains
│   └── Configuracion.java          # Beans de autenticación y usuarios
├── controller/                      # Controladores REST
│   ├── ApiController.java          # Endpoints de ejemplo con anotaciones
│   ├── AuthController.java         # Login y generación de JWT
│   └── HomeController.java         # Controlador web
├── dao/                            # Capa de datos
│   ├── UserEntity.java            # Entidad de usuario
│   ├── RolesEntity.java           # Entidad de roles
│   └── UserRepository.java        # Repositorio JPA
└── security/                       # Componentes de seguridad
    ├── IsAdmin.java               # Anotación personalizada @IsAdmin
    ├── IsUser.java                # Anotación personalizada @IsUser
    ├── IsAdminOrSelf.java         # Anotación personalizada @IsAdminOrSelf
    └── jwt/                       # Componentes JWT
        ├── JwtService.java        # Servicio para generar/validar tokens
        ├── JwtAuthenticationFilter.java  # Filtro de autenticación JWT
        └── CustomUserDetailsService.java # Carga usuarios desde BD
```

### Flujo de Autenticación

```
1. Cliente → POST /api/auth/login (username + password)
2. AuthController → AuthenticationManager valida credenciales
3. JwtService → Genera token JWT
4. Cliente recibe token
5. Cliente → Peticiones con header: Authorization: Bearer {token}
6. JwtAuthenticationFilter → Intercepta y valida el token
7. SecurityContextHolder → Establece la autenticación
8. Controlador → Verifica permisos con anotaciones
9. Cliente recibe respuesta
```

---

## 🔧 Configuración de Spring Security

### 1. SecurityConfig.java

Esta clase es el corazón de la configuración de seguridad. Define **dos SecurityFilterChains separados**:

#### **A) API REST SecurityFilterChain** (JWT + Stateless)

```java
@Bean
@Order(1)  // Prioridad alta - se evalúa primero
public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        // Solo aplica a rutas /api/**
        .securityMatcher("/api/**")
        .csrf(csrf -> csrf.disable())  // Sin CSRF para APIs REST
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/public", "/api/auth/**", "/api/jsr250-public").permitAll()
            .requestMatchers("/api/admin").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        // Modo STATELESS: sin sesiones
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // Añadir filtro JWT
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .httpBasic(Customizer.withDefaults());

    return http.build();
}
```

**Características clave:**
- `@Order(1)`: Se evalúa primero para rutas `/api/**`
- `CSRF deshabilitado`: No es necesario para APIs REST stateless
- `SessionCreationPolicy.STATELESS`: No se crean ni usan sesiones
- `JwtAuthenticationFilter`: Intercepta cada petición para validar el token JWT

#### **B) Web SecurityFilterChain** (FormLogin + Sesiones)

```java
@Bean
@Order(2)  // Prioridad menor - se evalúa después
public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/public", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login").permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/")
            .permitAll()
        );

    return http.build();
}
```

**Características clave:**
- `@Order(2)`: Se evalúa después del API SecurityFilterChain
- `FormLogin`: Formulario de login tradicional para aplicaciones web
- **Sesiones habilitadas**: Mantiene el estado de autenticación

### 2. Habilitación de Seguridad basada en Anotaciones

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,      // Habilita @Secured
    jsr250Enabled = true,       // Habilita @RolesAllowed, @PermitAll, @DenyAll
    proxyTargetClass = true     // Usa CGLIB proxy para clases concretas
)
public class SecurityConfig {
    // ...
}
```

**¿Qué hace cada opción?**
- `securedEnabled = true`: Permite usar `@Secured("ROLE_ADMIN")`
- `jsr250Enabled = true`: Permite usar anotaciones estándar Java EE (`@RolesAllowed`, `@PermitAll`, `@DenyAll`)
- `proxyTargetClass = true`: Usa proxies CGLIB para poder aplicar seguridad en clases concretas, no solo interfaces

---

## 🔐 Autenticación JWT

### 1. JwtService.java - Servicio de JWT

Clase responsable de **generar y validar tokens JWT**.

#### **Métodos principales:**

```java
// Generar token para un usuario
public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
}

// Extraer username del token
public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

// Validar token
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}
```

#### **Configuración en application.properties:**

```properties
# Clave secreta para firmar tokens (Base64, mínimo 256 bits)
jwt.secret=TXVsdGlDbGF2ZVNlY3JldGFTdXBlclNlZ3VyYVBhcmFKV1RRdWVEZWJlVGVuZXJBbE1lbm9zMjU2Qml0cw==

# Expiración del token: 24 horas (en milisegundos)
jwt.expiration=86400000
```

**⚠️ Importante:** En producción, la clave secreta debe estar en variables de entorno, no en el código.

### 2. JwtAuthenticationFilter.java - Filtro JWT

Intercepta **cada petición HTTP** para validar el token JWT.

#### **Flujo del filtro:**

```java
@Override
protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {
    
    // 1. Obtener header Authorization
    final String authHeader = request.getHeader("Authorization");
    
    // 2. Validar formato: "Bearer {token}"
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }
    
    // 3. Extraer el token
    final String jwt = authHeader.substring(7);
    
    // 4. Extraer username del token
    final String username = jwtService.extractUsername(jwt);
    
    // 5. Si hay username y no hay autenticación previa
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        
        // 6. Cargar usuario desde BD
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // 7. Validar token
        if (jwtService.isTokenValid(jwt, userDetails)) {
            
            // 8. Crear objeto de autenticación
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
            
            // 9. Establecer autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    
    // 10. Continuar con el siguiente filtro
    filterChain.doFilter(request, response);
}
```

**Características clave:**
- Hereda de `OncePerRequestFilter`: Se ejecuta una sola vez por petición
- No bloquea peticiones sin token (permite endpoints públicos)
- Establece la autenticación en `SecurityContextHolder` para que esté disponible en toda la aplicación

### 3. AuthController.java - Login

Endpoint para autenticar usuarios y obtener el token JWT.

```java
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
    
    // 1. Autenticar con username y password
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()
        )
    );
    
    // 2. Cargar detalles del usuario
    UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
    
    // 3. Generar token JWT
    String token = jwtService.generateToken(userDetails);
    
    // 4. Devolver respuesta con el token
    Map<String, Object> response = new HashMap<>();
    response.put("token", token);
    response.put("tipo", "Bearer");
    response.put("usuario", userDetails.getUsername());
    response.put("roles", userDetails.getAuthorities());
    
    return ResponseEntity.ok(response);
}
```

**Ejemplo de uso:**

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

# Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "usuario": "admin",
  "roles": ["ROLE_ADMIN", "ROLE_USER"]
}
```

### 4. CustomUserDetailsService.java

Carga usuarios desde la base de datos.

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
    // 1. Buscar usuario en la base de datos
    UserEntity user = userRepository.findByName(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    // 2. Convertir a UserDetails de Spring Security
    return User.builder()
        .username(user.getName())
        .password(user.getPassword())
        .roles(user.getRoles().stream()
            .map(RolesEntity::getRol)
            .collect(Collectors.joining(",")))
        .build();
}
```

---

## 🎯 Seguridad mediante Anotaciones

Spring Security ofrece varias formas de aplicar seguridad a nivel de método. Aquí están **todas las opciones disponibles** con ejemplos prácticos.

### 1. @Secured - Validación Simple de Roles

**Características:**
- ✅ Más simple y directa
- ✅ Solo valida roles
- ⚠️ Requiere prefijo `ROLE_`
- ⚠️ No permite expresiones complejas

```java
@Secured("ROLE_ADMIN")
@GetMapping("/secured-admin")
public Map<String, Object> securedAdminEndpoint(Authentication auth) {
    return Map.of(
        "mensaje", "Acceso con @Secured - Solo ADMIN",
        "usuario", auth.getName()
    );
}

// Múltiples roles (OR lógico)
@Secured({"ROLE_ADMIN", "ROLE_USER"})
@GetMapping("/secured-multiple")
public Map<String, Object> securedMultipleRoles(Authentication auth) {
    return Map.of("mensaje", "Admin o User pueden acceder");
}
```

**Cuándo usar @Secured:**
- ✅ Validaciones simples de roles
- ✅ Cuando no necesitas expresiones complejas
- ✅ Código legible y mantenible

### 2. @PreAuthorize - Validación Antes de Ejecutar

**Características:**
- ✅ Se evalúa **ANTES** de ejecutar el método
- ✅ Usa SpEL (Spring Expression Language)
- ✅ Permite expresiones complejas
- ✅ Puede acceder a parámetros del método
- ✅ No requiere prefijo `ROLE_` al usar `hasRole()`

#### **Ejemplo básico:**

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/preauthorize-admin")
public Map<String, Object> preAuthorizeAdminEndpoint(Authentication auth) {
    return Map.of("mensaje", "Solo ADMIN puede acceder");
}
```

#### **Expresiones complejas:**

```java
// OR lógico
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@GetMapping("/preauthorize-complex")
public Map<String, Object> preAuthorizeComplexEndpoint(Authentication auth) {
    return Map.of("mensaje", "ADMIN o USER pueden acceder");
}

// AND lógico
@PreAuthorize("hasRole('ADMIN') and hasRole('MANAGER')")
@GetMapping("/preauthorize-and")
public Map<String, Object> requiresBothRoles(Authentication auth) {
    return Map.of("mensaje", "Requiere AMBOS roles");
}
```

#### **Validación de parámetros:**

Una de las características más poderosas: validar parámetros del método.

```java
// Solo el propio usuario o un admin pueden acceder
@PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
@GetMapping("/user/{username}")
public Map<String, Object> getUserByUsername(@PathVariable String username, Authentication auth) {
    return Map.of(
        "mensaje", "Acceso a datos del usuario: " + username,
        "usuarioSolicitado", username,
        "usuarioAutenticado", auth.getName()
    );
}
```

**Explicación de la expresión:**
- `#username`: Accede al parámetro `username` del método
- `authentication.name`: Usuario autenticado actualmente
- `or`: Permite acceso si se cumple cualquiera de las condiciones

#### **Expresiones SpEL disponibles:**

| Expresión | Descripción |
|-----------|-------------|
| `hasRole('ADMIN')` | Usuario tiene el rol ADMIN |
| `hasAnyRole('ADMIN', 'USER')` | Usuario tiene cualquiera de los roles |
| `hasAuthority('WRITE')` | Usuario tiene el permiso WRITE |
| `isAuthenticated()` | Usuario está autenticado |
| `isAnonymous()` | Usuario es anónimo (no autenticado) |
| `principal` | Objeto UserDetails del usuario autenticado |
| `authentication` | Objeto Authentication completo |
| `#parametro` | Accede a parámetros del método |

**Cuándo usar @PreAuthorize:**
- ✅ Validaciones complejas con lógica OR/AND
- ✅ Cuando necesitas validar parámetros del método
- ✅ Reglas de negocio complejas
- ✅ Máxima flexibilidad

### 3. @PostAuthorize - Validación Después de Ejecutar

**Características:**
- ✅ Se evalúa **DESPUÉS** de ejecutar el método
- ✅ Puede validar el valor de retorno
- ✅ Útil para filtrar resultados
- ⚠️ El método se ejecuta siempre (consume recursos)

```java
// Valida el resultado antes de devolverlo
@PostAuthorize("returnObject.get('owner') == authentication.name or hasRole('ADMIN')")
@GetMapping("/postauthorize/{id}")
public Map<String, Object> postAuthorizeEndpoint(@PathVariable String id, Authentication auth) {
    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Recurso con ID: " + id);
    // Simula que el recurso pertenece al usuario "usuario"
    response.put("owner", "usuario");
    response.put("id", id);
    return response;
}
```

**Explicación:**
- El método se ejecuta completamente
- Se genera el `Map` de respuesta
- **Antes de devolver** el resultado, se valida la expresión
- Si `owner` del resultado no coincide con el usuario autenticado (y no es ADMIN), se lanza `AccessDeniedException`

**Expresiones disponibles:**
- `returnObject`: El valor que devuelve el método
- `returnObject.propiedad`: Acceder a propiedades del objeto retornado

**Cuándo usar @PostAuthorize:**
- ✅ Cuando necesitas validar el resultado del método
- ✅ Filtrar datos según el propietario
- ⚠️ Ten en cuenta que el método siempre se ejecuta (puede ser costoso)

### 4. @RolesAllowed - Estándar JSR-250

**Características:**
- ✅ Estándar Java EE (JSR-250)
- ✅ Portable entre frameworks
- ✅ **NO requiere** prefijo `ROLE_` (Spring lo añade automáticamente)
- ⚠️ Solo valida roles (como @Secured)

```java
@RolesAllowed("ADMIN")
@GetMapping("/jsr250-admin")
public Map<String, Object> jsr250AdminEndpoint(Authentication auth) {
    return Map.of(
        "mensaje", "Acceso con @RolesAllowed - Estándar JSR-250",
        "usuario", auth.getName()
    );
}

// Múltiples roles
@RolesAllowed({"ADMIN", "USER"})
@GetMapping("/jsr250-multiple")
public Map<String, Object> jsr250MultipleRolesEndpoint(Authentication auth) {
    return Map.of("mensaje", "Admin o User pueden acceder");
}
```

**Otras anotaciones JSR-250:**

#### **@PermitAll - Acceso público**

```java
@PermitAll
@GetMapping("/jsr250-public")
public Map<String, Object> jsr250PublicEndpoint() {
    return Map.of(
        "mensaje", "Acceso público - Todos pueden acceder",
        "timestamp", LocalDateTime.now()
    );
}
```

#### **@DenyAll - Denegar acceso a todos**

```java
@DenyAll
@GetMapping("/jsr250-deny")
public Map<String, Object> jsr250DenyEndpoint() {
    // Este método NUNCA se ejecutará
    return Map.of("mensaje", "Este endpoint está deshabilitado");
}
```

**Cuándo usar @RolesAllowed:**
- ✅ Cuando quieres código portable (no dependiente de Spring)
- ✅ Validaciones simples de roles
- ✅ Proyectos con estándares Java EE

---

## 🎨 Anotaciones Personalizadas

Una de las características más potentes es crear **tus propias anotaciones de seguridad**. Esto hace el código más legible y mantenible.

### 1. @IsAdmin

Anotación personalizada equivalente a `@PreAuthorize("hasRole('ADMIN')")`.

#### **Definición:**

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
public @interface IsAdmin {
}
```

#### **Uso:**

```java
@IsAdmin
@GetMapping("/custom-admin")
public Map<String, Object> customAdminEndpoint(Authentication auth) {
    return Map.of(
        "mensaje", "Acceso con @IsAdmin - Anotación personalizada",
        "usuario", auth.getName()
    );
}
```

**Ventajas:**
- ✅ Código más legible: `@IsAdmin` vs `@PreAuthorize("hasRole('ADMIN')")`
- ✅ Reutilizable en toda la aplicación
- ✅ Si cambias la lógica, solo modificas un lugar

### 2. @IsUser

Anotación para usuarios con rol USER.

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('USER')")
public @interface IsUser {
}
```

**Uso:**

```java
@IsUser
@GetMapping("/custom-user")
public Map<String, Object> customUserEndpoint(Authentication auth) {
    return Map.of(
        "mensaje", "Acceso con @IsUser",
        "usuario", auth.getName()
    );
}
```

### 3. @IsAdminOrSelf

Anotación más compleja: permite acceso si el usuario es ADMIN o si el parámetro `username` coincide con el usuario autenticado.

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
public @interface IsAdminOrSelf {
}
```

**Uso:**

```java
@IsAdminOrSelf
@GetMapping("/profile/{username}")
public Map<String, Object> getProfile(@PathVariable String username, Authentication auth) {
    return Map.of(
        "mensaje", "Perfil del usuario: " + username,
        "usuarioSolicitado", username,
        "usuarioAutenticado", auth.getName()
    );
}
```

**Casos de uso:**
- ✅ Usuario "juan" puede acceder a `/profile/juan`
- ✅ Admin puede acceder a cualquier perfil: `/profile/maria`
- ❌ Usuario "juan" NO puede acceder a `/profile/maria`

### 4. Crear tus propias anotaciones

Puedes crear anotaciones personalizadas para cualquier expresión compleja:

```java
// Ejemplo: Solo usuarios verificados
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() and principal.verified")
public @interface IsVerified {
}

// Ejemplo: Roles específicos de negocio
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('SALES', 'SALES_MANAGER')")
public @interface IsSalesPerson {
}
```

---

## 🔑 Clases Clave del Proyecto

### 1. SecurityConfig.java

**Responsabilidad:** Configurar las cadenas de filtros de seguridad.

**Configuración principal:**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,      // Habilita @Secured
    jsr250Enabled = true,       // Habilita @RolesAllowed, @PermitAll, @DenyAll
    proxyTargetClass = true     // Usa CGLIB proxy
)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    // Define dos SecurityFilterChains:
    // 1. Para API REST (/api/**)
    // 2. Para aplicación web (resto de URLs)
}
```

**Puntos clave:**
- Usa `@Order` para priorizar las cadenas de filtros
- Separa la seguridad de la API REST (stateless/JWT) de la web (sesiones/FormLogin)
- Inyecta `JwtAuthenticationFilter` para validar tokens

### 2. Configuracion.java

**Responsabilidad:** Configurar beans relacionados con autenticación.

```java
@Configuration
public class Configuracion {
    
    // AuthenticationProvider - cómo autenticar usuarios
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    // AuthenticationManager - orquesta la autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // Usuarios en memoria (para desarrollo/testing)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("usuario")
            .password(passwordEncoder().encode("password123"))
            .roles("USER")
            .build();
        
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN", "USER")
            .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }
    
    // Codificador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Componentes configurados:**
1. **AuthenticationProvider:** Define cómo se autentican los usuarios (en este caso, desde base de datos con DaoAuthenticationProvider)
2. **AuthenticationManager:** Orquesta el proceso de autenticación
3. **UserDetailsService:** Carga los detalles del usuario (en memoria o desde BD)
4. **PasswordEncoder:** BCrypt para hash seguro de contraseñas

### 3. JwtService.java

**Responsabilidad:** Generar y validar tokens JWT.

**Métodos principales:**

| Método | Descripción |
|--------|-------------|
| `generateToken(UserDetails)` | Genera un token JWT para el usuario |
| `extractUsername(String token)` | Extrae el username del token |
| `isTokenValid(String token, UserDetails)` | Valida si el token es válido |
| `extractClaim(String token, Function)` | Extrae un claim específico |
| `extractAllClaims(String token)` | Extrae todos los claims del token |

**Configuración:**
- Clave secreta: `jwt.secret` (application.properties)
- Expiración: `jwt.expiration` (milisegundos)
- Algoritmo: HS256

### 4. JwtAuthenticationFilter.java

**Responsabilidad:** Interceptar peticiones y validar tokens JWT.

**Flujo:**
1. Obtiene el header `Authorization`
2. Valida formato `Bearer {token}`
3. Extrae y valida el token
4. Carga el usuario desde `UserDetailsService`
5. Establece la autenticación en `SecurityContextHolder`
6. Continúa con el siguiente filtro

**Características:**
- Hereda de `OncePerRequestFilter` (se ejecuta una vez por petición)
- No bloquea peticiones sin token (permite endpoints públicos)

### 5. CustomUserDetailsService.java

**Responsabilidad:** Cargar usuarios desde la base de datos.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Buscar usuario en BD
        UserEntity user = userRepository.findByName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Convertir a UserDetails de Spring Security
        return User.builder()
            .username(user.getName())
            .password(user.getPassword())
            .roles(user.getRoles().stream()
                .map(RolesEntity::getRol)
                .collect(Collectors.joining(",")))
            .build();
    }
}
```

### 6. Entidades JPA

#### **UserEntity.java**

```java
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String password;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Set<RolesEntity> roles;
}
```

#### **RolesEntity.java**

```java
@Entity
@Table(name = "roles")
public class RolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String rol;
}
```

**Relación:** Muchos a Muchos (ManyToMany)
- Un usuario puede tener múltiples roles
- Un rol puede estar asignado a múltiples usuarios

---

## 💡 Casos de Uso y Ejemplos

### Caso 1: Endpoint Público

```java
@GetMapping("/api/public")
public Map<String, Object> publicEndpoint() {
    return Map.of(
        "mensaje", "Este es un endpoint público",
        "timestamp", LocalDateTime.now(),
        "autenticado", false
    );
}
```

**No requiere autenticación:** Definido en `SecurityConfig` con `permitAll()`.

### Caso 2: Endpoint Solo para ADMIN

```java
// Opción 1: Configurado en SecurityConfig
@GetMapping("/api/admin")
public Map<String, Object> adminEndpoint(Authentication auth) {
    return Map.of("mensaje", "Bienvenido Admin");
}

// Opción 2: Con anotación @Secured
@Secured("ROLE_ADMIN")
@GetMapping("/api/secured-admin")
public Map<String, Object> securedAdminEndpoint(Authentication auth) {
    return Map.of("mensaje", "Solo ADMIN con @Secured");
}

// Opción 3: Con anotación @PreAuthorize
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/preauthorize-admin")
public Map<String, Object> preAuthorizeAdminEndpoint(Authentication auth) {
    return Map.of("mensaje", "Solo ADMIN con @PreAuthorize");
}

// Opción 4: Con anotación personalizada
@IsAdmin
@GetMapping("/api/custom-admin")
public Map<String, Object> customAdminEndpoint(Authentication auth) {
    return Map.of("mensaje", "Solo ADMIN con @IsAdmin");
}
```

### Caso 3: Usuario puede acceder a sus propios datos, Admin a todos

```java
@PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
@GetMapping("/user/{username}")
public Map<String, Object> getUserByUsername(@PathVariable String username, Authentication auth) {
    return Map.of(
        "usuarioSolicitado", username,
        "usuarioAutenticado", auth.getName()
    );
}

// O con anotación personalizada:
@IsAdminOrSelf
@GetMapping("/profile/{username}")
public Map<String, Object> getProfile(@PathVariable String username, Authentication auth) {
    return Map.of(
        "usuarioSolicitado", username,
        "usuarioAutenticado", auth.getName()
    );
}
```

**Ejemplos:**
- Usuario "juan" solicita `/user/juan` → ✅ Permitido
- Usuario "juan" solicita `/user/maria` → ❌ Denegado (403 Forbidden)
- Admin solicita `/user/maria` → ✅ Permitido

### Caso 4: Validar el resultado del método

```java
@PostAuthorize("returnObject.get('owner') == authentication.name or hasRole('ADMIN')")
@GetMapping("/document/{id}")
public Map<String, Object> getDocument(@PathVariable String id) {
    // Simula cargar documento de BD
    Map<String, Object> document = new HashMap<>();
    document.put("id", id);
    document.put("owner", "usuario");  // Propietario del documento
    document.put("content", "Contenido del documento");
    
    return document;
    // Se valida ANTES de devolver:
    // Si el owner no coincide con el usuario autenticado (y no es ADMIN) → 403 Forbidden
}
```

### Caso 5: Login y uso del token JWT

#### **1. Login:**

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### **2. Respuesta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwOTQxMDAwMCwiZXhwIjoxNzA5NDk2NDAwfQ.signature",
  "tipo": "Bearer",
  "usuario": "admin",
  "roles": ["ROLE_ADMIN", "ROLE_USER"]
}
```

#### **3. Usar el token:**

```http
GET http://localhost:8080/api/private
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📊 Comparación de Anotaciones

| Anotación | Complejidad | Expresiones | Parámetros | Resultado | Estándar |
|-----------|-------------|-------------|------------|-----------|----------|
| `@Secured` | ⭐ Simple | ❌ No | ❌ No | ❌ No | Spring |
| `@PreAuthorize` | ⭐⭐⭐ Alta | ✅ SpEL | ✅ Sí | ❌ No | Spring |
| `@PostAuthorize` | ⭐⭐⭐ Alta | ✅ SpEL | ✅ Sí | ✅ Sí | Spring |
| `@RolesAllowed` | ⭐ Simple | ❌ No | ❌ No | ❌ No | Java EE |
| `@PermitAll` | ⭐ Simple | ❌ No | ❌ No | ❌ No | Java EE |
| `@DenyAll` | ⭐ Simple | ❌ No | ❌ No | ❌ No | Java EE |
| **Personalizadas** | ⭐⭐ Media | ✅ Sí | ✅ Sí | ✅ Sí | Proyecto |

---

## 🎓 Mejores Prácticas

### 1. Seguridad a nivel de método vs configuración

**✅ Usa anotaciones cuando:**
- La regla de seguridad es específica de un método
- Quieres que la seguridad esté cerca del código que protege
- Necesitas validar parámetros o resultados

**✅ Usa configuración (SecurityConfig) cuando:**
- La regla se aplica a múltiples endpoints con el mismo patrón
- Quieres centralizar la configuración
- Las reglas son simples (por URL)

### 2. Elegir la anotación correcta

- **Validaciones simples de roles:** `@Secured` o `@RolesAllowed`
- **Expresiones complejas:** `@PreAuthorize`
- **Validar parámetros:** `@PreAuthorize` con SpEL
- **Validar resultado:** `@PostAuthorize`
- **Código limpio y reutilizable:** Anotaciones personalizadas

### 3. Seguridad en capas

Combina múltiples niveles de seguridad:

```
1. SecurityFilterChain → Protege URLs por patrón
2. Anotaciones en controladores → Validaciones específicas
3. Anotaciones en servicios → Lógica de negocio protegida
```

### 4. Testing de seguridad

```java
@Test
@WithMockUser(username = "admin", roles = {"ADMIN"})
void testAdminEndpoint() {
    // Test con usuario ADMIN
}

@Test
@WithMockUser(username = "user", roles = {"USER"})
void testUserEndpointDenied() {
    // Test con usuario USER (debería fallar)
}
```

---

## 🔒 Seguridad en Producción

### 1. Variables de entorno

Nunca pongas claves secretas en el código:

```properties
# Mal (desarrollo)
jwt.secret=MiClaveSecreta123...

# Bien (producción)
jwt.secret=${JWT_SECRET}
```

### 2. HTTPS obligatorio

```java
http.requiresChannel(channel -> channel
    .anyRequest().requiresSecure()
);
```

### 3. Timeouts de tokens cortos

```properties
# Desarrollo: 24 horas
jwt.expiration=86400000

# Producción: 1 hora
jwt.expiration=3600000
```

### 4. Refresh tokens

Implementa refresh tokens para renovar tokens expirados sin pedir credenciales nuevamente.

### 5. Rate limiting

Limita el número de intentos de login para prevenir ataques de fuerza bruta.

---

## 📚 Recursos Adicionales

- **Documentación oficial:** https://spring.io/projects/spring-security
- **JWT:** https://jwt.io/
- **SpEL Reference:** https://docs.spring.io/spring-framework/reference/core/expressions.html

---

## 🎯 Resumen

Este proyecto demuestra:

1. **Dos enfoques de seguridad:**
   - API REST con JWT (stateless)
   - Web tradicional con sesiones (stateful)

2. **Múltiples formas de aplicar seguridad:**
   - Configuración centralizada (SecurityConfig)
   - Anotaciones estándar (@Secured, @RolesAllowed)
   - Anotaciones avanzadas (@PreAuthorize, @PostAuthorize)
   - Anotaciones personalizadas (@IsAdmin, @IsUser, @IsAdminOrSelf)

3. **Arquitectura completa:**
   - Autenticación JWT
   - Validación de tokens con filtros
   - Integración con base de datos
   - Usuarios y roles

4. **Mejores prácticas:**
   - Separación de responsabilidades
   - Código reutilizable
   - Seguridad en capas
   - Configuración flexible

---

**Autor:** Proyecto de ejemplo Spring Security  
**Fecha:** 2026  
**Versión:** 1.0

