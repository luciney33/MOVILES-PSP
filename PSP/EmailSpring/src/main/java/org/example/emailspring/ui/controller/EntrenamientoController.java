package org.example.emailspring.ui.controller;

import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Entrenamiento;
import org.example.emailspring.domain.service.EntrenamientoService;
import org.example.emailspring.ui.interceptor.RequiresAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constantes.API_ENTRENAMIENTOS)
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    public EntrenamientoController(EntrenamientoService entrenamientoService) {
        this.entrenamientoService = entrenamientoService;
    }

    @GetMapping
    @RequiresAuth
    public ResponseEntity<List<Entrenamiento>> listar() {
        return ResponseEntity.ok(entrenamientoService.getAll());
    }

    @GetMapping(Constantes.PATH_ID)
    @RequiresAuth(admin = true)
    public ResponseEntity<Entrenamiento> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.getById(id));

    }

    @RequiresAuth(admin = true)
    @PostMapping
    public ResponseEntity<Entrenamiento> crear(@RequestBody Entrenamiento entrenamiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrenamientoService.save(entrenamiento));
    }

    @RequiresAuth(admin = true)
    @PutMapping(Constantes.PATH_ID)
    public ResponseEntity<Entrenamiento> actualizar(@PathVariable Long id, @RequestBody Entrenamiento entrenamiento) {
        return ResponseEntity.ok(entrenamientoService.update(id, entrenamiento));

    }


    @RequiresAuth(admin = true)
    @DeleteMapping(Constantes.PATH_ID)
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        entrenamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
