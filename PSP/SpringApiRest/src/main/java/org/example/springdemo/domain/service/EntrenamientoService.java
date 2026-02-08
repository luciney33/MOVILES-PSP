package org.example.springdemo.domain.service;

import org.example.springdemo.domain.mapper.EntrenamientoMapper;
import org.example.springdemo.domain.model.Entrenamiento;
import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.data.EntrenamientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntrenamientoService {
    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoMapper entrenamientoMapper;

    public EntrenamientoService(EntrenamientoRepository entrenamientoRepository,
                                EntrenamientoMapper entrenamientoMapper) {
        this.entrenamientoRepository = entrenamientoRepository;
        this.entrenamientoMapper = entrenamientoMapper;
    }


    public List<Entrenamiento> getAll() {
     return entrenamientoRepository.findAll()
             .stream().map(entrenamientoMapper::toDomain).toList();
    }

    public Entrenamiento getById(Long id) {
        return entrenamientoRepository.findById(id)
                .map(entrenamientoMapper::toDomain)
                .orElse(null);
    }

    public Entrenamiento save(Entrenamiento entrenamiento) {
        EntrenamientoEntity saved = entrenamientoRepository.save(entrenamientoMapper.toEntity(entrenamiento));
        return entrenamientoMapper.toDomain(saved);
    }
    public Entrenamiento update(Long id, Entrenamiento entrenamiento) {
        return entrenamientoRepository.findById(id)
                .map(existing -> {
                    EntrenamientoEntity updated = entrenamientoMapper.toEntity(entrenamiento);
                    updated.setId(existing.getId());
                    return entrenamientoMapper.toDomain(entrenamientoRepository.save(updated));
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (entrenamientoRepository.existsById(id)) {
            entrenamientoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Entrenamiento> getByUserId(int userId) {
        return entrenamientoRepository.getByUsuarioId(userId)
                .stream()
                .map(entrenamientoMapper::toDomain)
                .toList();
    }

    public List<Entrenamiento> getByNombre(String nombre) {
        return entrenamientoRepository.getByNombre(nombre)
                .stream()
                .map(entrenamientoMapper::toDomain)
                .toList();
    }
}
