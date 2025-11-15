package org.example.springdemo.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioEntity {
    private int id;
    private String username;
    private String password;
    private String email;
    private String nombre;
    private String rol;

    public UsuarioEntity() {
    }
}