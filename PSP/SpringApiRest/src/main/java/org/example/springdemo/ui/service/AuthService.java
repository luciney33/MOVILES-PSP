package org.example.springdemo.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.Constantes;
import org.example.springdemo.data.entity.UsuarioEntity;
import org.example.springdemo.domain.model.Rol;
import org.example.springdemo.domain.model.Usuario;
import org.example.springdemo.domain.service.UsuarioService;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UsuarioService usuarioService;

    public AuthService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario login(String username, String password, HttpSession session) {
        Usuario usuario = usuarioService.login(username, password);
        if (usuario != null)
            session.setAttribute(Constantes.SESSION_ATTR_USUARIO, usuario);
        return usuario;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(Constantes.SESSION_USUARIO_ID) != null;
    }
    public Long getUsuarioFromSession(HttpSession session) {
        return ((Usuario) session.getAttribute(Constantes.SESSION_ATTR_USUARIO)).id();
    }
    public Rol getRolFromSession(HttpSession session) {
        return session.getAttribute(Constantes.SESSION_ATTR_USUARIO) != null
                ? ((Usuario) session.getAttribute(Constantes.SESSION_ATTR_USUARIO)).rol()
                : null;
    }
    public boolean isAdmin(HttpSession session) {
        Rol rol = getRolFromSession(session);
        return Rol.ADMIN.equals(rol);
    }
}
