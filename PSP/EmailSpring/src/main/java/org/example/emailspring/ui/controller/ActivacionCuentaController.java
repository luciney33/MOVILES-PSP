package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.ui.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(Constantes.API_ACTIVAR)
@Tag(name = Constantes.TAG_ACTIVACION_CUENTA, description = Constantes.TAG_ACTIVACION_CUENTA_DESC)
public class ActivacionCuentaController {
    private final AuthService authService;

    public ActivacionCuentaController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping
    @Operation(summary = Constantes.OP_ACTIVAR_CUENTA, description = Constantes.OP_ACTIVAR_CUENTA_DESC)
    public String template(@Parameter(description = Constantes.PARAM_CODIGO_ACTIVACION_DESC) @RequestParam(Constantes.CODIGO) String codigoActivacion, Model model) {

        Usuario usuario = authService.activarCuenta(codigoActivacion);

        model.addAttribute(Constantes.NOMBRE_USUARIO, usuario.nombre());

        return Constantes.TEMPLATE;
    }
}

