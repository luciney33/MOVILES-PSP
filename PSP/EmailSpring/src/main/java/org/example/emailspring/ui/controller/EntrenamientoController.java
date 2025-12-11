package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Entrenamiento;
import org.example.emailspring.domain.service.EntrenamientoService;
import org.example.emailspring.ui.interceptor.RequiresAuth;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constantes.API_ENTRENAMIENTOS)
@Tag(name = "Entrenamientos", description = "Gestión de entrenamientos y rutinas (requiere autenticación).")
@SecurityRequirement(name = "sessionCookieAuth")
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    public EntrenamientoController(EntrenamientoService entrenamientoService) {
        this.entrenamientoService = entrenamientoService;
    }

    @GetMapping
    @RequiresAuth
    @Operation(summary = "Listar todos los entrenamientos", description = "Permite a usuarios autenticados ver todos los entrenamientos disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista de entrenamientos recuperada con éxito")
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<@NonNull List<Entrenamiento>> listar() {
        return ResponseEntity.ok(entrenamientoService.getAll());
    }

    @GetMapping(Constantes.PATH_ID)
    @RequiresAuth(admin = true)
    @Operation(summary = "Obtener entrenamiento por ID", description = "Recupera un entrenamiento específico. Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrenamiento encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es admin)", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = Constantes.NO_ENCONTRADO, content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<@NonNull Entrenamiento> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.getById(id));

    }

    @RequiresAuth(admin = true)
    @PostMapping
    @Operation(summary = "Crear un nuevo entrenamiento", description = "Crea un nuevo registro de entrenamiento. Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrenamiento creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es admin)", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<@NonNull Entrenamiento> crear(@RequestBody Entrenamiento entrenamiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrenamientoService.save(entrenamiento));
    }

    @RequiresAuth(admin = true)
    @PutMapping(Constantes.PATH_ID)
    @Operation(summary = "Actualizar entrenamiento existente", description = "Actualiza los detalles de un entrenamiento por su ID. Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrenamiento actualizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es admin)", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = Constantes.NO_ENCONTRADO, content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<@NonNull Entrenamiento> actualizar(@PathVariable Long id, @RequestBody Entrenamiento entrenamiento) {
        return ResponseEntity.ok(entrenamientoService.update(id, entrenamiento));

    }


    @RequiresAuth(admin = true)
    @DeleteMapping(Constantes.PATH_ID)
    @Operation(summary = "Eliminar entrenamiento", description = "Elimina un entrenamiento por su ID. Solo accesible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entrenamiento eliminado (sin contenido de respuesta)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (no es admin)", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = Constantes.NO_ENCONTRADO, content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<@NonNull Void> borrar(@PathVariable Long id) {
        entrenamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
