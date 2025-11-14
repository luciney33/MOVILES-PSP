package org.example.springdemo.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ejercicios")
public class EjercicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entrenamiento_id", nullable = false)
    private EntrenamientoEntity entrenamiento;

    @Column(nullable = false)
    private String nombre;

    @Column
    private Integer repeticiones;

    @Column
    private Integer series;

    public EjercicioEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EntrenamientoEntity getEntrenamiento() { return entrenamiento; }
    public void setEntrenamiento(EntrenamientoEntity entrenamiento) { this.entrenamiento = entrenamiento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }
}
