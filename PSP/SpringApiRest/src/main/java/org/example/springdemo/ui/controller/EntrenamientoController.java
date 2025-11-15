package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.constantes;
import org.example.springdemo.domain.model.Entrenamiento;
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

    public EntrenamientoController(EntrenamientoService entrenamientoService) {
        this.entrenamientoService = entrenamientoService;
    }

    @GetMapping
    public ResponseEntity<List<Entrenamiento>> listar(HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Entrenamiento> list = entrenamientoService.getAll(session);
        return ResponseEntity.ok(list);
    }

    @GetMapping(constantes.PATH_ID)
    public ResponseEntity<Entrenamiento> getById(@PathVariable int id, HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Entrenamiento> opt = entrenamientoService.getById(id, session);
        return opt.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping
    public ResponseEntity<Entrenamiento> crear(@RequestBody Entrenamiento entrenamiento, HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Entrenamiento createdAdmi = entrenamientoService.save(entrenamiento, session);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmi);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(constantes.PATH_ID)
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody Entrenamiento entrenamiento, HttpSession session) {
        if (!entrenamientoService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!entrenamientoService.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        entrenamientoService.update(new Entrenamiento(id, entrenamiento.usuarioId(), entrenamiento.nombre(), entrenamiento.descripcion()), session);
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
