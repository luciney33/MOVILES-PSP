package org.example.springdemo.data.entity;
import java.util.List;

public class EntrenamientoEntity {
    private Long id;
    private Long userId;
    private String nombre;
    private String descripcion;
    private List<EjercicioEntity> ejercicios;


    public EntrenamientoEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<EjercicioEntity> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<EjercicioEntity> ejercicios) { this.ejercicios = ejercicios; }
}
