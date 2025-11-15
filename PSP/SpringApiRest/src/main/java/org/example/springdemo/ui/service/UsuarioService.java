package org.example.springdemo.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.constantes;
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
            session.setAttribute(constantes.SESSION_USUARIO_ID, entity.getId());
            session.setAttribute("rol", entity.getRol());
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(constantes.SESSION_USUARIO_ID) != null;
    }

    public Optional<Usuario> getUsuarioFromSession(HttpSession session) {
        Integer usuarioIdSessionn = (Integer) session.getAttribute(constantes.SESSION_USUARIO_ID);
        if (usuarioIdSessionn == null) return Optional.empty();

        int usuarioId = usuarioIdSessionn;

        UsuarioEntity entity = usuarioRepository.getById(usuarioId);

        if (entity == null) {
            session.removeAttribute(constantes.SESSION_USUARIO_ID);
            return Optional.empty();
        }

        return Optional.of(usuarioMapper.toDomain(entity));
    }


    public boolean isAdmin(HttpSession session) {
        return getUsuarioFromSession(session)
                .map(usu -> "ADMIN".equals(usu.rol()))
                .orElse(false);
    }

}
