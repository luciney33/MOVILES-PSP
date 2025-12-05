package org.example.emailspring.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Entrenamiento;
import org.example.emailspring.domain.service.EntrenamientoService;
import org.example.emailspring.ui.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constantes.API_ENTRENAMIENTOS)
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;
    private final AuthService authService;

    public EntrenamientoController(EntrenamientoService entrenamientoService, AuthService authService) {
        this.authService = authService;
        this.entrenamientoService = entrenamientoService;
    }

    @GetMapping
    public ResponseEntity<List<Entrenamiento>> listar(HttpSession session) {
        if (authService.isAuthenticated(session)) {
            if (authService.isAdmin(session)) {
                return ResponseEntity.ok(entrenamientoService.getAll());
            } else {
                int userId = authService.getUsuarioFromSession(session).intValue();
                return ResponseEntity.ok(entrenamientoService.getByUsuarioId(userId));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(Constantes.PATH_ID)
    public ResponseEntity<Entrenamiento> getById(@PathVariable Long id, HttpSession session) {
        if (!authService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Entrenamiento entrenamiento = entrenamientoService.getById(id);
        if (entrenamiento == null) {
            return ResponseEntity.notFound().build();
        }

        int userId = authService.getUsuarioFromSession(session).intValue();
        if (authService.isAdmin(session) || entrenamiento.usuarioId() == userId) {
            return ResponseEntity.ok(entrenamiento);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<Entrenamiento> crear(@RequestBody Entrenamiento entrenamiento, HttpSession session) {
        if (authService.isAuthenticated(session)) {
            if (authService.isAdmin(session)) {
                Entrenamiento newReno = entrenamientoService.save(entrenamiento);
                return ResponseEntity.status(HttpStatus.CREATED).body(newReno);
            }
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PutMapping(Constantes.PATH_ID)
    public ResponseEntity<Entrenamiento> actualizar(@PathVariable Long id, @RequestBody Entrenamiento entrenamiento, HttpSession session) {
        if (authService.isAuthenticated(session)) {
            if (authService.isAdmin(session)) {
                Entrenamiento updated = entrenamientoService.update(id, entrenamiento);
                if (updated != null) {
                    return ResponseEntity.ok(updated);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping(Constantes.PATH_ID)
    public ResponseEntity<Void> borrar(@PathVariable Long id, HttpSession session) {
        if (!authService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (authService.isAdmin(session)) {
            return entrenamientoService.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        }

        Entrenamiento entrenamiento = entrenamientoService.getById(id);
        if (entrenamiento == null) {
            return ResponseEntity.notFound().build();
        }

        int userId = authService.getUsuarioFromSession(session).intValue();
        if (entrenamiento.usuarioId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return entrenamientoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
