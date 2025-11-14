package org.example.springdemo.data.entity;



public class EjercicioEntity {
    private Long id;
    private EntrenamientoEntity entrenamiento;
    private String nombre;
    private Integer repeticiones;
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
