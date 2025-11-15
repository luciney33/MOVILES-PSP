package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.constantes;
import org.example.springdemo.domain.model.Ejercicio;
import org.example.springdemo.ui.dto.EjercicioDTO;
import org.example.springdemo.ui.mapper.EjercicioDtoMapper;
import org.example.springdemo.ui.service.EjercicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(constantes.API_EJERCICIOS)
public class EjercicioController {

    private final EjercicioService ejercicioService;
    private final EjercicioDtoMapper ejercicioDtoMapper;

    public EjercicioController(EjercicioService ejercicioService, EjercicioDtoMapper ejercicioDtoMapper) {
        this.ejercicioService = ejercicioService;
        this.ejercicioDtoMapper = ejercicioDtoMapper;
    }

    @GetMapping
    public ResponseEntity<List<EjercicioDTO>> listar(HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Ejercicio> list = ejercicioService.getAll(session);
        List<EjercicioDTO> dtoList = list.stream()
                .map(ejercicioDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(constantes.PATH_ID)
    public ResponseEntity<EjercicioDTO> getById(@PathVariable int id, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Ejercicio> opt = ejercicioService.getById(id, session);
        return opt.map(ejercicioDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping
    public ResponseEntity<EjercicioDTO> crear(@RequestBody EjercicioDTO ejercicioDTO, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Ejercicio domain = ejercicioDtoMapper.fromDto(ejercicioDTO);
            Ejercicio creado = ejercicioService.save(domain, session);
            return ResponseEntity.status(HttpStatus.CREATED).body(ejercicioDtoMapper.toDto(creado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(constantes.PATH_ID)
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody EjercicioDTO ejercicioDTO, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!ejercicioService.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Ejercicio toUpdate = new Ejercicio(id, ejercicioDTO.entrenamientoId(), ejercicioDTO.nombre(), ejercicioDTO.repeticiones(), ejercicioDTO.series());
        ejercicioService.update(toUpdate, session);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(constantes.PATH_ID)
    public ResponseEntity<Void> borrar(@PathVariable int id, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean borrado = ejercicioService.delete(id, session);
        if (borrado) return ResponseEntity.noContent().build();
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
