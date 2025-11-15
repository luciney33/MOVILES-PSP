package org.example.springdemo.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.data.entity.EjercicioEntity;
import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.data.mapper.EjercicioMapDomain;
import org.example.springdemo.data.repository.EjercicioRepository;
import org.example.springdemo.data.repository.EntrenamientoRepository;
import org.example.springdemo.domain.model.Ejercicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EjercicioService {
    private final EjercicioRepository ejercicioRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final EjercicioMapDomain ejercicioMapper;
    private final UsuarioService usuarioService;

    public EjercicioService(EjercicioRepository ejercicioRepository,
                            EntrenamientoRepository entrenamientoRepository,
                            EjercicioMapDomain ejercicioMapper,
                            UsuarioService usuarioService) {
        this.ejercicioRepository = ejercicioRepository;
        this.entrenamientoRepository = entrenamientoRepository;
        this.ejercicioMapper = ejercicioMapper;
        this.usuarioService = usuarioService;
    }

    public List<Ejercicio> getAll(HttpSession session) {
        if (usuarioService.isAdmin(session)) {
            return ejercicioRepository.getAll()
                    .stream()
                    .map(ejercicioMapper::toDomain)
                    .collect(Collectors.toList());
        } else {
            int usuarioId = (int) session.getAttribute("usuarioId");
            // Solo ejercicios de los entrenamientos del usuario
            return entrenamientoRepository.getByUsuarioId(usuarioId).stream()
                    .flatMap(ent -> ejercicioRepository.getByEntrenamientoId(ent.getId()).stream())
                    .map(ejercicioMapper::toDomain)
                    .collect(Collectors.toList());
        }
    }

    public Optional<Ejercicio> getById(int id, HttpSession session) {
        EjercicioEntity entity = ejercicioRepository.getById(id);
        if (entity == null) return Optional.empty();

        if (usuarioService.isAdmin(session)) {
            return Optional.of(ejercicioMapper.toDomain(entity));
        }

        int usuarioId = (int) session.getAttribute("usuarioId");
        EntrenamientoEntity entrenamiento = entrenamientoRepository.getById(entity.getEntrenamientoId());
        if (entrenamiento != null && entrenamiento.getUsuarioId() == usuarioId) {
            return Optional.of(ejercicioMapper.toDomain(entity));
        }

        return Optional.empty();
    }

    @Transactional
    public Ejercicio save(Ejercicio ejercicio, HttpSession session) {
        if (!usuarioService.isAdmin(session)) {
            throw new RuntimeException("No tiene permisos para crear ejercicio");
        }
        EjercicioEntity entity = ejercicioMapper.toEntity(ejercicio);
        int id = ejercicioRepository.save(entity);
        entity.setId(id);
        return ejercicioMapper.toDomain(entity);
    }

    @Transactional
    public void update(Ejercicio ejercicio, HttpSession session) {
        if (!usuarioService.isAdmin(session)) {
            throw new RuntimeException("No tiene permisos para actualizar ejercicio");
        }
        ejercicioRepository.update(ejercicioMapper.toEntity(ejercicio));
    }

    @Transactional
    public boolean delete(int id, HttpSession session) {
        EjercicioEntity entity = ejercicioRepository.getById(id);
        if (entity == null) return false;

        if (usuarioService.isAdmin(session)) {
            return ejercicioRepository.delete(id);
        }

        int usuarioId = (int) session.getAttribute("usuarioId");
        EntrenamientoEntity entrenamiento = entrenamientoRepository.getById(entity.getEntrenamientoId());
        if (entrenamiento != null && entrenamiento.getUsuarioId() == usuarioId) {
            return ejercicioRepository.delete(id);
        }

        return false;
    }
}
