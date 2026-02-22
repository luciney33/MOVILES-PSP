# ESPECIFICACIONES PARA EL SERVIDOR - FUNCIONALIDADES FALTANTES

## 📋 RESUMEN EJECUTIVO

El cliente Android para secretos cifrados está **100% implementado** pero requiere que el servidor implemente dos endpoints adicionales para funcionalidad completa:

1. **GET /api/secretos** - Listar todos los secretos del usuario (propios + compartidos)
2. **Modificar SecretoCifradoResponse** - Incluir lista de usuarios con quien está compartido

---

## 🎯 FUNCIONALIDAD 1: LISTAR SECRETOS DEL USUARIO

### **Problema Actual**

El servidor **NO tiene** un endpoint para listar los secretos de un usuario. El cliente Android no puede mostrar la pantalla `ListaSecretosScreen` porque no hay forma de obtener:
- Secretos creados por el usuario (autor)
- Secretos compartidos con el usuario (destinatario)

### **Endpoint Requerido**

```
GET /api/secretos
```

### **Descripción**

Retorna un listado resumido de todos los secretos accesibles para el usuario autenticado, incluyendo:
- Secretos propios (donde es autor)
- Secretos compartidos (donde es destinatario)

---

### **IMPLEMENTACIÓN DETALLADA**

#### **1. Crear DTO de Respuesta: `SecretoSummaryDTO.java`**

```java
package com.example.gymapp.model.dto;

import java.time.LocalDateTime;

/**
 * DTO ligero para listar secretos.
 * NO incluye el contenido cifrado (solo metadata).
 */
public record SecretoSummaryDTO(
    Long id,
    Long autorId,
    String autorUsername,
    String autorNombre,
    LocalDateTime fechaCreacion,
    Boolean esAutor,           // true si el usuario actual es el autor
    Integer cantidadCompartidos // Cantidad de usuarios con quien está compartido
) {}
```

**Razón:** No se debe enviar el contenido cifrado en un listado. Solo información resumida para mostrar en cards.

---

#### **2. Agregar Queries en `SecretoRepository`**

```java
package com.example.gymapp.repository;

import com.example.gymapp.model.SecretoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecretoRepository extends JpaRepository<SecretoEntity, Long> {
    
    // Query existente...
    Optional<SecretoEntity> findById(Long id);
    
    /**
     * Obtiene todos los secretos donde el usuario es el autor
     */
    @Query("SELECT s FROM SecretoEntity s WHERE s.autor.username = :username")
    List<SecretoEntity> findByAutorUsername(@Param("username") String username);
    
    /**
     * Obtiene todos los secretos donde el usuario es el autor
     * (alternativa sin @Query)
     */
    List<SecretoEntity> findByAutorUsername(String username);
}
```

---

#### **3. Agregar Query en `SecretoCompartidoRepository`**

```java
package com.example.gymapp.repository;

import com.example.gymapp.model.SecretoCompartidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretoCompartidoRepository extends JpaRepository<SecretoCompartidoEntity, Long> {
    
    // Queries existentes...
    Optional<SecretoCompartidoEntity> findBySecretoIdAndDestinatarioId(Long secretoId, Long destinatarioId);
    void deleteBySecretoIdAndSecretoAutorIdAndDestinatarioId(Long secretoId, Long autorId, Long destinatarioId);
    
    /**
     * Obtiene todos los secretos compartidos con un usuario específico
     */
    @Query("SELECT sc FROM SecretoCompartidoEntity sc WHERE sc.destinatario.username = :username")
    List<SecretoCompartidoEntity> findByDestinatarioUsername(@Param("username") String username);
    
    /**
     * Cuenta cuántos usuarios tienen acceso a un secreto
     */
    @Query("SELECT COUNT(sc) FROM SecretoCompartidoEntity sc WHERE sc.secreto.id = :secretoId")
    Long countBySecretoId(@Param("secretoId") Long secretoId);
    
    /**
     * Obtiene todos los compartidos de un secreto
     */
    List<SecretoCompartidoEntity> findBySecretoId(Long secretoId);
}
```

---

#### **4. Agregar Método en `SecretoService.java`**

```java
package com.example.gymapp.service;

import com.example.gymapp.model.SecretoEntity;
import com.example.gymapp.model.SecretoCompartidoEntity;
import com.example.gymapp.model.dto.SecretoSummaryDTO;
import com.example.gymapp.repository.SecretoRepository;
import com.example.gymapp.repository.SecretoCompartidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecretoService {
    
    private final SecretoRepository secretoRepository;
    private final SecretoCompartidoRepository compartidoRepository;
    // ... otros campos
    
    /**
     * Lista todos los secretos accesibles para un usuario
     * @param username Username del usuario autenticado
     * @return Lista de secretos (propios + compartidos)
     */
    @Transactional(readOnly = true)
    public List<SecretoSummaryDTO> listarSecretos(String username) {
        List<SecretoSummaryDTO> resultado = new ArrayList<>();
        
        // 1. Obtener secretos propios (donde es autor)
        List<SecretoEntity> secretosPropios = secretoRepository.findByAutorUsername(username);
        
        for (SecretoEntity secreto : secretosPropios) {
            Long cantidadCompartidos = compartidoRepository.countBySecretoId(secreto.getId());
            
            SecretoSummaryDTO dto = new SecretoSummaryDTO(
                secreto.getId(),
                secreto.getAutor().getId(),
                secreto.getAutor().getUsername(),
                secreto.getAutor().getNombre(),
                secreto.getFechaCreacion(),
                true,  // esAutor = true
                cantidadCompartidos.intValue()
            );
            
            resultado.add(dto);
        }
        
        // 2. Obtener secretos compartidos (donde es destinatario)
        List<SecretoCompartidoEntity> secretosCompartidos = 
            compartidoRepository.findByDestinatarioUsername(username);
        
        for (SecretoCompartidoEntity compartido : secretosCompartidos) {
            SecretoEntity secreto = compartido.getSecreto();
            
            SecretoSummaryDTO dto = new SecretoSummaryDTO(
                secreto.getId(),
                secreto.getAutor().getId(),
                secreto.getAutor().getUsername(),
                secreto.getAutor().getNombre(),
                compartido.getFechaCompartido() != null 
                    ? compartido.getFechaCompartido() 
                    : secreto.getFechaCreacion(),
                false, // esAutor = false
                0      // No mostrar cantidad de compartidos en secretos ajenos
            );
            
            resultado.add(dto);
        }
        
        // 3. Ordenar por fecha (más recientes primero)
        resultado.sort((a, b) -> b.fechaCreacion().compareTo(a.fechaCreacion()));
        
        return resultado;
    }
    
    // ... otros métodos existentes
}
```

---

#### **5. Agregar Endpoint en `SecretoController.java`**

```java
package com.example.gymapp.controller;

import com.example.gymapp.model.dto.SecretoSummaryDTO;
import com.example.gymapp.service.SecretoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secretos")
@Tag(name = "Secretos", description = "Gestión de secretos cifrados")
@SecurityRequirement(name = "cookieAuth")
public class SecretoController {
    
    private final SecretoService secretoService;
    
    public SecretoController(SecretoService secretoService) {
        this.secretoService = secretoService;
    }
    
    /**
     * 🆕 NUEVO ENDPOINT - Listar secretos del usuario
     */
    @GetMapping
    @Operation(
        summary = "Listar secretos del usuario",
        description = "Retorna todos los secretos accesibles para el usuario autenticado: " +
                      "secretos propios (donde es autor) y secretos compartidos con él. " +
                      "NO incluye el contenido cifrado, solo metadata para listar."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de secretos recuperada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<List<SecretoSummaryDTO>> listarSecretos(
            @AuthenticationPrincipal UserDetails loginUser) {
        
        List<SecretoSummaryDTO> secretos = secretoService.listarSecretos(loginUser.getUsername());
        return ResponseEntity.ok(secretos);
    }
    
    // ... otros endpoints existentes (crear, obtener, compartir, etc.)
}
```

---

#### **6. Modificar Entidad `SecretoCompartidoEntity` (OPCIONAL)**

Si aún no existe el campo `fechaCompartido`, agregarlo:

```java
package com.example.gymapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "secreto_compartido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecretoCompartidoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secreto_id", nullable = false)
    private SecretoEntity secreto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private UsuarioEntity destinatario;
    
    @Lob
    @Column(name = "clave_simetrica_cifrada_destinatario", nullable = false)
    private byte[] claveSimetricaCifradaDestinatario;
    
    /**
     * 🆕 AGREGAR ESTE CAMPO si no existe
     * Fecha en que se compartió el secreto con este usuario
     */
    @Column(name = "fecha_compartido")
    private LocalDateTime fechaCompartido;
    
    @PrePersist
    protected void onCreate() {
        if (fechaCompartido == null) {
            fechaCompartido = LocalDateTime.now();
        }
    }
}
```

**Script de migración (Flyway/Liquibase):**

```sql
-- V2__add_fecha_compartido.sql
ALTER TABLE secreto_compartido 
ADD COLUMN fecha_compartido TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Actualizar registros existentes
UPDATE secreto_compartido 
SET fecha_compartido = CURRENT_TIMESTAMP 
WHERE fecha_compartido IS NULL;
```

---

### **EJEMPLO DE RESPUESTA JSON**

```json
GET /api/secretos

[
  {
    "id": 1,
    "autorId": 5,
    "autorUsername": "juan",
    "autorNombre": "Juan Pérez",
    "fechaCreacion": "2024-01-15T10:30:00",
    "esAutor": true,
    "cantidadCompartidos": 3
  },
  {
    "id": 2,
    "autorId": 5,
    "autorUsername": "juan",
    "autorNombre": "Juan Pérez",
    "fechaCreacion": "2024-01-14T08:15:00",
    "esAutor": true,
    "cantidadCompartidos": 0
  },
  {
    "id": 7,
    "autorId": 8,
    "autorUsername": "maria",
    "autorNombre": "María García",
    "fechaCreacion": "2024-01-16T14:20:00",
    "esAutor": false,
    "cantidadCompartidos": 0
  }
]
```

---

## 🎯 FUNCIONALIDAD 2: LISTA DE USUARIOS COMPARTIDOS

### **Problema Actual**

El endpoint `GET /api/secretos/{id}` retorna `SecretoCifradoResponse` que **NO incluye** la lista de usuarios con quien está compartido el secreto. Solo retorna un `Boolean esCompartido`.

### **Impacto**

El cliente Android no puede:
- Mostrar "Compartido con: Juan, María, Carlos" en la UI
- Implementar botones de "Revocar acceso" individuales de forma práctica
- Mostrar información completa del secreto

---

### **IMPLEMENTACIÓN DETALLADA**

#### **1. Modificar `SecretoCifradoResponse.java`**

```java
package com.example.gymapp.model.dto;

import java.util.List;

/**
 * Response para obtener un secreto cifrado individual.
 * Incluye toda la información necesaria para descifrar y mostrar.
 */
public record SecretoCifradoResponse(
    Long id,
    Long autorId,
    String autorUsername,
    String autorNombre,
    String contenidoCifrado,      // Base64
    String claveAESCifrada,       // Base64
    String firma,                 // Base64
    String iv,                    // Base64
    String autorClavePublica,     // Base64
    Boolean esCompartido,
    
    /**
     * 🆕 NUEVO CAMPO
     * Lista de usuarios con quien está compartido el secreto.
     * Solo se llena si el usuario actual es el AUTOR.
     * Si es un secreto compartido (el usuario NO es autor), viene vacío.
     */
    List<UsuarioPublicoDTO> compartidoCon
) {}
```

---

#### **2. Modificar el Método `obtener()` en `SecretoController.java`**

```java
@GetMapping("/{id}")
@Operation(
    summary = "Obtener secreto cifrado",
    description = "Retorna el secreto tal como está cifrado. El cliente debe descifrar y verificar la firma. " +
                  "Si el usuario es el autor, también retorna la lista de usuarios con quien está compartido."
)
public ResponseEntity<SecretoCifradoResponse> obtener(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails loginUser) {
    
    // Obtener el secreto (verifica acceso)
    SecretoEntity secreto = secretoService.obtener(loginUser.getUsername(), id);
    
    // Obtener la clave AES cifrada correspondiente
    byte[] claveAESCifrada = secretoService.obtenerClaveAESCifrada(loginUser.getUsername(), id);
    
    // Determinar si el usuario es el autor
    boolean esCompartido = !secreto.getAutor().getUsername().equals(loginUser.getUsername());
    
    // 🆕 NUEVO: Obtener lista de usuarios compartidos (solo si es el autor)
    List<UsuarioPublicoDTO> compartidoCon = new ArrayList<>();
    
    if (!esCompartido) { // Es el autor
        List<SecretoCompartidoEntity> compartidos = 
            compartidoRepository.findBySecretoId(secreto.getId());
        
        compartidoCon = compartidos.stream()
            .map(c -> new UsuarioPublicoDTO(
                c.getDestinatario().getId(),
                c.getDestinatario().getUsername(),
                c.getDestinatario().getNombre(),
                null, // No necesita clave pública aquí
                null  // No necesita certificado aquí
            ))
            .toList();
    }
    // Si NO es el autor (esCompartido = true), la lista queda vacía
    
    // Construir response
    SecretoCifradoResponse response = new SecretoCifradoResponse(
        secreto.getId(),
        secreto.getAutor().getId(),
        secreto.getAutor().getUsername(),
        secreto.getAutor().getNombre(),
        Base64.getEncoder().encodeToString(secreto.getContenidoCifrado()),
        Base64.getEncoder().encodeToString(claveAESCifrada),
        Base64.getEncoder().encodeToString(secreto.getFirma()),
        Base64.getEncoder().encodeToString(secreto.getIv()),
        Base64.getEncoder().encodeToString(secreto.getAutor().getClavePublica()),
        esCompartido,
        compartidoCon  // 🆕 NUEVO CAMPO
    );
    
    return ResponseEntity.ok(response);
}
```

---

#### **3. Asegurar que `UsuarioPublicoDTO` ya existe**

```java
package com.example.gymapp.model.dto;

/**
 * DTO con información pública de un usuario.
 * NO incluye información sensible.
 */
public record UsuarioPublicoDTO(
    Long id,
    String username,
    String nombre,
    String publicKey,    // Base64, puede ser null
    String certificado   // Base64, puede ser null
) {}
```

---

### **EJEMPLO DE RESPUESTA JSON**

#### **Caso 1: Usuario ES el autor**

```json
GET /api/secretos/1

{
  "id": 1,
  "autorId": 5,
  "autorUsername": "juan",
  "autorNombre": "Juan Pérez",
  "contenidoCifrado": "8f3h2j...",
  "claveAESCifrada": "k4j2h8...",
  "firma": "9j3h2k...",
  "iv": "3h2j4k...",
  "autorClavePublica": "MIIBIj...",
  "esCompartido": false,
  "compartidoCon": [
    {
      "id": 8,
      "username": "maria",
      "nombre": "María García",
      "publicKey": null,
      "certificado": null
    },
    {
      "id": 12,
      "username": "carlos",
      "nombre": "Carlos López",
      "publicKey": null,
      "certificado": null
    }
  ]
}
```

#### **Caso 2: Usuario NO es el autor (secreto compartido con él)**

```json
GET /api/secretos/7

{
  "id": 7,
  "autorId": 8,
  "autorUsername": "maria",
  "autorNombre": "María García",
  "contenidoCifrado": "3k2j4h...",
  "claveAESCifrada": "j4h2k8...",
  "firma": "2j3h4k...",
  "iv": "4h2j3k...",
  "autorClavePublica": "MIIBIj...",
  "esCompartido": true,
  "compartidoCon": []
}
```

**Nota:** La lista viene vacía porque el usuario NO es el autor, por seguridad no se muestra con quién más está compartido.

---

## 📊 RESUMEN DE CAMBIOS EN EL SERVIDOR

### **Archivos a Crear:**

1. ✅ `SecretoSummaryDTO.java` - DTO para listado

### **Archivos a Modificar:**

1. ✅ `SecretoRepository.java` - Agregar query `findByAutorUsername()`
2. ✅ `SecretoCompartidoRepository.java` - Agregar queries:
   - `findByDestinatarioUsername()`
   - `countBySecretoId()`
   - `findBySecretoId()`
3. ✅ `SecretoService.java` - Agregar método `listarSecretos()`
4. ✅ `SecretoController.java` - Agregar endpoint `GET /api/secretos`
5. ✅ `SecretoController.java` - Modificar endpoint `GET /api/secretos/{id}` para incluir `compartidoCon`
6. ✅ `SecretoCifradoResponse.java` - Agregar campo `List<UsuarioPublicoDTO> compartidoCon`
7. ⚠️ `SecretoCompartidoEntity.java` - (OPCIONAL) Agregar campo `fechaCompartido`

### **Migraciones de Base de Datos:**

```sql
-- OPCIONAL: Solo si se agrega fechaCompartido
ALTER TABLE secreto_compartido 
ADD COLUMN fecha_compartido TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

UPDATE secreto_compartido 
SET fecha_compartido = CURRENT_TIMESTAMP 
WHERE fecha_compartido IS NULL;
```

---

## ✅ VERIFICACIÓN DE COMPATIBILIDAD

### **Cliente Android ya está preparado para:**

1. ✅ Consumir `GET /api/secretos`
2. ✅ Parsear `List<SecretoSummaryDTO>`
3. ✅ Mostrar lista en `ListaSecretosScreen`
4. ✅ Parsear `compartidoCon` en `SecretoCifradoResponse`
5. ✅ Mostrar usuarios compartidos en `VerSecretoScreen`
6. ✅ Implementar botones de revocar acceso individual

### **Lo único que falta es implementar estos cambios en el servidor.**

---

## 🔒 CONSIDERACIONES DE SEGURIDAD

### **1. Control de Acceso**

✅ **Listar Secretos:**
- Solo muestra secretos donde el usuario es autor O destinatario
- Usa `@AuthenticationPrincipal` para identificar usuario
- No expone contenido cifrado, solo metadata

✅ **Lista de Compartidos:**
- Solo se muestra si el usuario es el AUTOR
- Si no es autor, la lista viene vacía (privacidad)
- No expone información sensible de otros destinatarios

### **2. Rendimiento**

⚠️ **Optimización recomendada:**

```java
// En listarSecretos(), hacer una sola query para contar compartidos
@Query("SELECT s.id, COUNT(sc) FROM SecretoEntity s " +
       "LEFT JOIN SecretoCompartidoEntity sc ON sc.secreto.id = s.id " +
       "WHERE s.autor.username = :username " +
       "GROUP BY s.id")
Map<Long, Long> countCompartidosByAutor(@Param("username") String username);
```

Esto evita N+1 queries.

### **3. Privacidad**

✅ **No se expone:**
- Contenido cifrado en listados
- Claves públicas en lista de compartidos (innecesario)
- Con quién está compartido un secreto si no eres el autor

---

## 🎉 RESULTADO FINAL

Una vez implementados estos cambios, el cliente Android tendrá **funcionalidad 100% completa**:

✅ Registro con generación de claves RSA-4096  
✅ Crear secretos cifrados end-to-end  
✅ **Listar todos los secretos del usuario** *(propios + compartidos)*  
✅ Ver y descifrar secretos individuales  
✅ **Ver lista completa de usuarios con quien está compartido**  
✅ Compartir con múltiples usuarios simultáneamente  
✅ **Revocar acceso individual de forma práctica** *(con UI intuitiva)*  
✅ Eliminar secretos (autor) con revocación automática  
✅ Verificación completa de certificados y firmas digitales  

---

## 📚 NOTAS DE IMPLEMENTACIÓN

### **Paquetes Recomendados**
```
com.example.gymapp.model.dto/       → SecretoSummaryDTO, SecretoCifradoResponse
com.example.gymapp.repository/      → SecretoRepository, SecretoCompartidoRepository
com.example.gymapp.service/         → SecretoService
com.example.gymapp.controller/      → SecretoController
```

### **Dependencias Necesarias**
- Spring Boot 3.x
- Spring Security con JWT/Cookie Auth
- Spring Data JPA
- PostgreSQL/MySQL (base de datos)
- Lombok (opcional, para @Data)

### **Flujo de Pruebas Recomendado**

1. **Usuario A crea secreto** → aparece en su lista con `cantidadCompartidos = 0`
2. **Usuario A comparte con Usuario B** → `cantidadCompartidos = 1`
3. **Usuario B lista secretos** → ve el secreto compartido con `esAutor = false`
4. **Usuario A obtiene secreto** → ve a Usuario B en `compartidoCon[]`
5. **Usuario B obtiene secreto** → `compartidoCon[]` vacío (no es autor, privacidad)
6. **Usuario A revoca acceso a B** → `cantidadCompartidos = 0`
7. **Usuario B lista secretos** → ya no ve el secreto

---

**¡Con estos cambios el sistema de secretos cifrados estará 100% funcional y seguro!** 🔒✨
