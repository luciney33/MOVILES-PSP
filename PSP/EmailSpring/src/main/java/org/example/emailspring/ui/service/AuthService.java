package org.example.emailspring.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Rol;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.domain.service.UsuarioService;
import org.example.emailspring.ui.dto.UsuarioDTO;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UsuarioService usuarioService;

    public AuthService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario login(String username, String password, HttpSession session) {
        Usuario usuario = usuarioService.login(username, password, session);
            session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        return usuario;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public Usuario register(UsuarioDTO usuario) {
        return usuarioService.register(usuario);
    }

    public Usuario activarCuenta(String codigoActivacion) {
        return usuarioService.activarCuenta(codigoActivacion);
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(Constantes.SESSION_USUARIO_ID) != null;
    }

    public Long getUsuarioIdFromSession(HttpSession session) {
        return ((Usuario) session.getAttribute(Constantes.SESSION_USUARIO_ID)).id();
    }

    public Rol getRolFromSession(HttpSession session) {
        return session.getAttribute(Constantes.SESSION_USUARIO_ID) != null
                ? ((Usuario) session.getAttribute(Constantes.SESSION_USUARIO_ID)).rol()
                : null;
    }
    public boolean isAdmin(HttpSession session) {
        return Rol.ADMIN.equals(getRolFromSession(session));
    }
}
