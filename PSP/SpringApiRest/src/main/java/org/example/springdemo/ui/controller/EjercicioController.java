package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.constantes;
import org.example.springdemo.domain.model.Ejercicio;
import org.example.springdemo.ui.service.EjercicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(constantes.API_EJERCICIOS)
public class EjercicioController {

    private final EjercicioService ejercicioService;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }

    @GetMapping
    public ResponseEntity<List<Ejercicio>> listar(HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ejercicioService.getAll(session));
    }

    @GetMapping(constantes.PATH_ID)
    public ResponseEntity<Ejercicio> getById(@PathVariable int id, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Ejercicio> opt = ejercicioService.getById(id, session);
        return opt.map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping
    public ResponseEntity<Ejercicio> crear(@RequestBody Ejercicio body, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Ejercicio created = ejercicioService.save(body, session);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(constantes.PATH_ID)
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody Ejercicio body, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!ejercicioService.isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ejercicioService.update(new Ejercicio(id, body.entrenamientoId(), body.nombre(), body.repeticiones(), body.series()), session);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(constantes.PATH_ID)
    public ResponseEntity<Void> borrar(@PathVariable int id, HttpSession session) {
        if (!ejercicioService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean deleted = ejercicioService.delete(id, session);
        if (deleted) return ResponseEntity.noContent().build();
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
