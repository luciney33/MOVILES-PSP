package org.example.springdemo.data.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EjercicioEntity {
    private int id;
    private int entrenamientoId;
    private String nombre;
    private int repeticiones;
    private int series;


    public EjercicioEntity() {}

}
