package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.service.SecretoService;
import org.example.emailspring.ui.dto.CompartirRequest;
import org.example.emailspring.ui.dto.SecretoRequest;
import org.example.emailspring.ui.dto.SecretoResponse;
import org.example.emailspring.ui.dto.VerSecretoRequest;
import org.example.emailspring.ui.security.IsUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constantes.API_SECRETOS)
@Tag(name = Constantes.TAG_SECRETOS, description = Constantes.TAG_SECRETOS_DESC)
@SecurityRequirement(name = Constantes.SECURITY_SESSION_COOKIE_AUTH)
public class SecretoController {

    private final UsuarioRepository usuarioRepository;
    private final SecretoService secretoService;

    public SecretoController(UsuarioRepository usuarioRepository, SecretoService secretoService) {
        this.usuarioRepository = usuarioRepository;
        this.secretoService = secretoService;
    }

    @IsUser
    @PostMapping
    @Operation(summary = Constantes.OP_CREAR_SECRETO)
    public ResponseEntity<SecretoResponse> guardar(@RequestBody SecretoRequest request,
                                          @AuthenticationPrincipal UserDetails loginUser) throws Exception {
        UsuarioEntity autor = usuarioRepository.findByUsername(loginUser.getUsername());
        Long idGenerado = secretoService.guardar(autor.getId(), request.password(), request.contenido());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SecretoResponse(idGenerado, request.contenido(), autor.getNombre(), false));

    }

    @IsUser
    @PostMapping(Constantes.PATH_SECRETO_VER)
    @Operation(summary = Constantes.OP_VER_SECRETO)
    public ResponseEntity<SecretoResponse> ver(@PathVariable Long id,
                                               @RequestBody VerSecretoRequest request,
                                               @AuthenticationPrincipal UserDetails loginUser) throws Exception {
        String contenidoPlano = secretoService.verPorUsername(loginUser.getUsername(), request.password(), id);
        return ResponseEntity.ok(new SecretoResponse(id, contenidoPlano, Constantes.MSG_AUTOR_VERIFICADO, true));
    }

    @IsUser
    @PostMapping(Constantes.PATH_SECRETO_COMPARTIR)
    @Operation(summary = Constantes.OP_COMPARTIR_SECRETO)
    public ResponseEntity<String> compartir(@PathVariable Long id,
                                            @RequestBody CompartirRequest request,
                                            @AuthenticationPrincipal UserDetails loginUser) throws Exception {
        secretoService.compartirPorUsername(loginUser.getUsername(), request.password(), id, request.receptorId());
        return ResponseEntity.ok(Constantes.MSG_SECRETO_COMPARTIDO_EXITO);
    }

    @IsUser
    @DeleteMapping(Constantes.PATH_SECRETO_REVOCAR)
    @Operation(summary = Constantes.OP_REVOCAR_ACCESO_SECRETO)
    public ResponseEntity<Void> revocar(@PathVariable Long id,
                                        @PathVariable Long receptorId,
                                        @AuthenticationPrincipal UserDetails loginUser) {
        secretoService.revocarPorUsername(loginUser.getUsername(), id, receptorId);
        return ResponseEntity.noContent().build();
    }
}