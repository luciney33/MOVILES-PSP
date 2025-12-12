package org.example.emailspring.data.entity;

import jakarta.persistence.*;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Rol;

import java.time.LocalDateTime;


@Entity
@Table(name = Constantes.TABLE_USUARIOS)
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String nombre;
    @Column
    private boolean activo;
    @Column
    private String codigoActivacion;
    @Column
    private LocalDateTime expiracionCodigo;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public UsuarioEntity() {}

    public UsuarioEntity(Long id, String username, String password, String email, String nombre, Rol rol, boolean activo, String codigoActivacion, LocalDateTime expiracionCodigo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = activo;
        this.codigoActivacion = codigoActivacion;
        this.expiracionCodigo = expiracionCodigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoActivacion() {
        return codigoActivacion;
    }

    public boolean activo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setCodigoActivacion(String codigoActivacion) {
        this.codigoActivacion = codigoActivacion;
    }

    public LocalDateTime getExpiracionCodigo() {
        return expiracionCodigo;
    }

    public void setExpiracionCodigo(LocalDateTime expiracionCodigo) {
        this.expiracionCodigo = expiracionCodigo;
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
