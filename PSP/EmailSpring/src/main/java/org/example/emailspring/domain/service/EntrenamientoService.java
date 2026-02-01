package org.example.emailspring.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.EntrenamientoRepository;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.EntrenamientoEntity;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.mapper.EntrenamientoMapper;
import org.example.emailspring.domain.model.Entrenamiento;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntrenamientoService {
    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoMapper entrenamientoMapper;
    private final UsuarioRepository usuarioRepository;

    public EntrenamientoService(EntrenamientoRepository entrenamientoRepository,
                                EntrenamientoMapper entrenamientoMapper,
                                UsuarioRepository usuarioRepository) {
        this.entrenamientoRepository = entrenamientoRepository;
        this.entrenamientoMapper = entrenamientoMapper;
        this.usuarioRepository = usuarioRepository;
    }


    public List<Entrenamiento> getAll() {
     return entrenamientoRepository.findAll()
             .stream().map(entrenamientoMapper::toDomain).toList();
    }

    public Entrenamiento getById(Long id) {
        return entrenamientoRepository.findById(id)
                .map(entrenamientoMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException(Constantes.NO_ENCONTRADO));
    }

    public Entrenamiento save(Entrenamiento entrenamiento) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException(Constantes.MSG_NO_USUARIO_AUTENTICADO);
        }
        String username = authentication.getName();

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new UsernameNotFoundException(Constantes.MSG_USUARIO_NO_ENCONTRADO_CON_NOMBRE + username);
        }

        EntrenamientoEntity entity = entrenamientoMapper.toEntity(entrenamiento);
        entity.setUsuarioId(usuarioEntity.getId());

        EntrenamientoEntity saved = entrenamientoRepository.save(entity);
        return entrenamientoMapper.toDomain(saved);
    }
    public Entrenamiento update(Long id, Entrenamiento entrenamiento) {
        return entrenamientoRepository.findById(id)
                .map(existing -> {
                    EntrenamientoEntity updated = entrenamientoMapper.toEntity(entrenamiento);
                    updated.setId(existing.getId());
                    updated.setUsuarioId(existing.getUsuarioId()); // Mantener el usuario original
                    return entrenamientoMapper.toDomain(entrenamientoRepository.save(updated));
                })
                .orElseThrow(() -> new EntityNotFoundException(Constantes.NO_ENCONTRADO));
    }

    public void delete(Long id) {
        if (entrenamientoRepository.existsById(id)) {
            entrenamientoRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(Constantes.NO_ENCONTRADO);
        }
    }
}
