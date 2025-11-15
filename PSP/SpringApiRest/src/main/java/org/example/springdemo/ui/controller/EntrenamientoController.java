package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.constantes;
import org.example.springdemo.domain.model.Entrenamiento;
import org.example.springdemo.ui.dto.EntrenamientoDTO;
import org.example.springdemo.ui.mapper.EntrenamientoDtoMapper;
import org.example.springdemo.ui.service.EntrenamientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(constantes.API_ENTRENAMIENTOS)
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;
    private final EntrenamientoDtoMapper entrenamientoDtoMapper;

    public EntrenamientoController(EntrenamientoService entrenamientoService, EntrenamientoDtoMapper entrenamientoDtoMapper) {
        this.entrenamientoService = entrenamientoService;
        this.entrenamientoDtoMapper = entrenamientoDtoMapper;
    }

    @GetMapping
    public ResponseEntity<List<EntrenamientoDTO>> listar(HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Entrenamiento> list = entrenamientoService.getAll(session);
        List<EntrenamientoDTO> dtoList = entrenamientoDtoMapper.toDtoList(list);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(constantes.PATH_ID)
    public ResponseEntity<EntrenamientoDTO> getById(@PathVariable int id, HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Entrenamiento> opt = entrenamientoService.getById(id, session);
        return opt
                .map(entrenamientoDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping
    public ResponseEntity<EntrenamientoDTO> crear(@RequestBody EntrenamientoDTO entrenamientoDTO, HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Entrenamiento domain = entrenamientoDtoMapper.toDomain(entrenamientoDTO);
            Entrenamiento creado = entrenamientoService.save(domain, session);
            EntrenamientoDTO createdDto = entrenamientoDtoMapper.toDto(creado);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @PutMapping(constantes.PATH_ID)
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody EntrenamientoDTO entrenamientoDTO, HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!entrenamientoService.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Entrenamiento domain = entrenamientoDtoMapper.toDomain(entrenamientoDTO);
        Entrenamiento toUpdate = new Entrenamiento(id, domain.usuarioId(), domain.nombre(), domain.descripcion());
        entrenamientoService.update(toUpdate, session);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(constantes.PATH_ID)
    public ResponseEntity<Void> borrar(@PathVariable int id, HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean borrado = entrenamientoService.delete(id, session);
        if (borrado) return ResponseEntity.noContent().build();
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
