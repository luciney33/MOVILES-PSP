package org.example.emailspring.ui.controller;

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
public class ActivacionCuentaController {
    private final AuthService authService;

    public ActivacionCuentaController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping
    public String template(@RequestParam(Constantes.CODIGO) String codigoActivacion, Model model) {

        Usuario usuario = authService.activarCuenta(codigoActivacion);

        model.addAttribute(Constantes.NOMBRE_USUARIO, usuario.nombre());

        return Constantes.TEMPLATE;
    }
}

