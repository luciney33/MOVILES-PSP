package org.example.springdemo.data.entity;

import jakarta.persistence.*;
import org.example.springdemo.common.Constantes;
import org.example.springdemo.domain.model.Rol;


@Entity
@Table(name = Constantes.TABLE_USUARIOS)
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public UsuarioEntity() {}

    public UsuarioEntity(Long id, String username, String password, String email, String nombre, Rol rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
