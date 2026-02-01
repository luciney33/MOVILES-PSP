package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Entrenamiento;
import org.example.emailspring.domain.service.EntrenamientoService;
import org.example.emailspring.ui.security.IsAdmin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constantes.API_ENTRENAMIENTOS)
@Tag(name = Constantes.TAG_ENTRENAMIENTOS, description = Constantes.TAG_ENTRENAMIENTOS_DESC)
@SecurityRequirement(name = Constantes.SECURITY_SESSION_COOKIE_AUTH)
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    public EntrenamientoController(EntrenamientoService entrenamientoService) {
        this.entrenamientoService = entrenamientoService;
    }

    @GetMapping
    @PreAuthorize(Constantes.SECURITY_IS_AUTHENTICATED)
    @Operation(summary = Constantes.OP_LISTAR_ENTRENAMIENTOS, description = Constantes.OP_LISTAR_ENTRENAMIENTOS_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_LISTA_ENTRENAMIENTOS_RECUPERADA)
    @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.RESP_NO_AUTORIZADO, content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<List<Entrenamiento>> listar() {
        return ResponseEntity.ok(entrenamientoService.getAll());
    }

    @GetMapping(Constantes.PATH_ID)
    @PreAuthorize(Constantes.SECURITY_IS_AUTHENTICATED)
    @Operation(summary = Constantes.OP_OBTENER_ENTRENAMIENTO, description = Constantes.OP_OBTENER_ENTRENAMIENTO_DESC)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_ENTRENAMIENTO_ENCONTRADO),
            @ApiResponse(responseCode = Constantes.HTTP_403, description = Constantes.RESP_ACCESO_DENEGADO_NO_ADMIN, content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = Constantes.HTTP_404, description = Constantes.NO_ENCONTRADO, content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Entrenamiento> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.getById(id));
    }

    @PostMapping
    @IsAdmin
    @Operation(summary = Constantes.OP_CREAR_ENTRENAMIENTO, description = Constantes.OP_USAR_ANOTACION_IS_ADMIN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_201, description = Constantes.RESP_ENTRENAMIENTO_CREADO),
            @ApiResponse(responseCode = Constantes.HTTP_400, description = Constantes.RESP_DATOS_INVALIDOS, content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = Constantes.HTTP_403, description = Constantes.RESP_ACCESO_DENEGADO_NO_ADMIN, content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Entrenamiento> crear(@RequestBody Entrenamiento entrenamiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrenamientoService.save(entrenamiento));
    }

    @PutMapping(Constantes.PATH_ID)
    @Secured(Constantes.SECURITY_ROLE_ADMIN_VALUE)
    @Operation(summary = Constantes.OP_ACTUALIZAR_ENTRENAMIENTO, description = Constantes.OP_USAR_ANOTACION_SECURED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_ENTRENAMIENTO_ACTUALIZADO),
            @ApiResponse(responseCode = Constantes.HTTP_403, description = Constantes.RESP_ACCESO_DENEGADO_NO_ADMIN, content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = Constantes.HTTP_404, description = Constantes.NO_ENCONTRADO, content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Entrenamiento> actualizar(@PathVariable Long id, @RequestBody Entrenamiento entrenamiento) {
        return ResponseEntity.ok(entrenamientoService.update(id, entrenamiento));
    }

    @DeleteMapping(Constantes.PATH_ID)
    @RolesAllowed(Constantes.ADMIN)
    @Operation(summary = Constantes.OP_ELIMINAR_ENTRENAMIENTO, description = Constantes.OP_USAR_ANOTACION_ROLES_ALLOWED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_204, description = Constantes.RESP_ENTRENAMIENTO_ELIMINADO),
            @ApiResponse(responseCode = Constantes.HTTP_403, description = Constantes.RESP_ACCESO_DENEGADO_NO_ADMIN, content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = Constantes.HTTP_404, description = Constantes.NO_ENCONTRADO, content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        entrenamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
