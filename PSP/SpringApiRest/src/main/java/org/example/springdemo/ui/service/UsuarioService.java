package org.example.springdemo.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.data.entity.UsuarioEntity;
import org.example.springdemo.data.mapper.UsuarioMapDomain;
import org.example.springdemo.data.repository.UsuarioRepository;
import org.example.springdemo.domain.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapDomain usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioMapDomain usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public Optional<Usuario> login(String username, String password, HttpSession session) {
        UsuarioEntity entity = usuarioRepository.getByUsername(username);
        if (entity != null && passwordEncoder.matches(password, entity.getPassword())) {
            Usuario usuario = usuarioMapper.toDomain(entity);
            session.setAttribute("usuarioId", entity.getId());
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("usuario") != null;
    }

    public Optional<Usuario> getUsuarioFromSession(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) return Optional.empty();

        UsuarioEntity entity = usuarioRepository.getById(usuarioId);
        if (entity == null) return Optional.empty();

        return Optional.of(usuarioMapper.toDomain(entity));
    }
    public boolean isAdmin(HttpSession session) {
        return getUsuarioFromSession(session)
                .map(usu -> "ADMIN".equals(usu.rol()))
                .orElse(false);
    }

    public boolean isUser(HttpSession session) {
        return getUsuarioFromSession(session)
                .map(usu -> "USER".equals(usu.rol()))
                .orElse(false);
    }

}
