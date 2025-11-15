package org.example.springdemo.data.repository;

import org.example.springdemo.data.entity.UsuarioEntity;

import java.util.List;

public interface UsuarioRepository {
    List<UsuarioEntity> getAll();
    UsuarioEntity getById(int id);
    UsuarioEntity getByUsername(String username);
    int save(UsuarioEntity usuario);
    void update(UsuarioEntity usuario);
    boolean delete(int id);
}
