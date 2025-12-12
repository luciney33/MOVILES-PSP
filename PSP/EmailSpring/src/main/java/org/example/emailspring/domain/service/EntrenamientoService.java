package org.example.emailspring.domain.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.EntrenamientoRepository;
import org.example.emailspring.data.entity.EntrenamientoEntity;
import org.example.emailspring.domain.mapper.EntrenamientoMapper;
import org.example.emailspring.domain.model.Entrenamiento;
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
                .orElseThrow(() -> new EntityNotFoundException(Constantes.NO_ENCONTRADO));
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
