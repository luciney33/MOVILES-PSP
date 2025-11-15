package org.example.springdemo.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.data.entity.UsuarioEntity;
import org.example.springdemo.data.mapper.UsuarioMapDomain;
import org.example.springdemo.data.repository.EjercicioRepository;
import org.example.springdemo.data.repository.EntrenamientoRepository;
import org.example.springdemo.data.repository.UsuarioRepository;
import org.example.springdemo.domain.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final EjercicioRepository ejercicioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapDomain usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, EntrenamientoRepository entrenamientoRepository, EjercicioRepository ejercicioRepository, PasswordEncoder passwordEncoder, UsuarioMapDomain usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.entrenamientoRepository = entrenamientoRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }


    @Transactional
    public boolean deleteUsuario(int usuarioId) {
        List<EntrenamientoEntity> entrenamientos =
                entrenamientoRepository.getByUsuarioId(usuarioId);

        for (EntrenamientoEntity ent : entrenamientos) {
            ejercicioRepository.deleteByEntrenamientoId(ent.getId());
        }

        entrenamientoRepository.deleteByUsuarioId(usuarioId);

        return usuarioRepository.delete(usuarioId);
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
        return session.getAttribute("usuarioId") != null;
    }

    public Optional<Usuario> getUsuarioFromSession(HttpSession session) {
        int usuarioId = (int) session.getAttribute("usuarioId");
        if (usuarioId == 0) return Optional.empty();

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
