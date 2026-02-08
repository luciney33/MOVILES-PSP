package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Ejercicio;
import org.example.emailspring.domain.service.EjercicioService;
import org.example.emailspring.ui.security.IsUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constantes.API_EJERCICIOS)
@Tag(name = Constantes.TAG_EJERCICIOS, description = Constantes.TAG_EJERCICIOS_DESC)
@SecurityRequirement(name = Constantes.SECURITY_SESSION_COOKIE_AUTH)
public class EjercicioController {
    private final EjercicioService ejerService;
    public EjercicioController(EjercicioService ejerService) {
        this.ejerService = ejerService;
    }

    @GetMapping
    @IsUser
    @Operation(summary = Constantes.OP_LISTAR_EJERCICIOS, description = Constantes.OP_LISTAR_EJERCICIOS_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_LISTA_ENTRENAMIENTOS_RECUPERADA)
    @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.RESP_NO_AUTORIZADO, content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<List<Ejercicio>> listarEjercicios() {
        return ResponseEntity.ok(ejerService.getAll());
    }
}
