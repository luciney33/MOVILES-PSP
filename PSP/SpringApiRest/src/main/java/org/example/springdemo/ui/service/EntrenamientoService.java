package org.example.springdemo.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.data.mapper.EntrenamientoMapDomain;
import org.example.springdemo.data.repository.EjercicioRepository;
import org.example.springdemo.data.repository.EntrenamientoRepository;
import org.example.springdemo.domain.model.Entrenamiento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntrenamientoService {
    private final EntrenamientoRepository entrenamientoRepository;
    private final EjercicioRepository ejercicioRepository;
    private final EntrenamientoMapDomain entrenamientoMapper;
    private final UsuarioService usuarioService;

    public EntrenamientoService(EntrenamientoRepository entrenamientoRepository,
                                EjercicioRepository ejercicioRepository,
                                EntrenamientoMapDomain entrenamientoMapper,
                                UsuarioService usuarioService) {
        this.entrenamientoRepository = entrenamientoRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.entrenamientoMapper = entrenamientoMapper;
        this.usuarioService = usuarioService;
    }

    public List<Entrenamiento> getAll(HttpSession session) {
        if (usuarioService.isAdmin(session)) {
            return entrenamientoRepository.getAll()
                    .stream()
                    .map(entrenamientoMapper::toDomain)
                    .collect(Collectors.toList());
        } else {
            int usuarioId = (int) session.getAttribute("usuarioId");
            return entrenamientoRepository.getByUsuarioId(usuarioId)
                    .stream()
                    .map(entrenamientoMapper::toDomain)
                    .collect(Collectors.toList());
        }
    }

    public Optional<Entrenamiento> getById(int id, HttpSession session) {
        EntrenamientoEntity entity = entrenamientoRepository.getById(id);
        if (entity == null) return Optional.empty();

        if (usuarioService.isAdmin(session) || entity.getUsuarioId() == (int) session.getAttribute("usuarioId")) {
            return Optional.of(entrenamientoMapper.toDomain(entity));
        }

        return Optional.empty();
    }

    @Transactional
    public Entrenamiento save(Entrenamiento entrenamiento, HttpSession session) {
        if (!usuarioService.isAdmin(session)) {
            throw new RuntimeException("No tiene permisos para crear entrenamiento");
        }
        EntrenamientoEntity entity = entrenamientoMapper.toEntity(entrenamiento);
        int id = entrenamientoRepository.save(entity);
        entity.setId(id);
        return entrenamientoMapper.toDomain(entity);
    }

    @Transactional
    public void update(Entrenamiento entrenamiento, HttpSession session) {
        if (!usuarioService.isAdmin(session)) {
            throw new RuntimeException("No tiene permisos para actualizar entrenamiento");
        }
        entrenamientoRepository.update(entrenamientoMapper.toEntity(entrenamiento));
    }

    @Transactional
    public boolean delete(int id, HttpSession session) {
        EntrenamientoEntity entity = entrenamientoRepository.getById(id);
        if (entity == null) return false;

        if (usuarioService.isAdmin(session) || entity.getUsuarioId() == (int) session.getAttribute("usuarioId")) {
            ejercicioRepository.deleteByEntrenamientoId(id);
            return entrenamientoRepository.delete(id);
        }

        return false;
    }
}
