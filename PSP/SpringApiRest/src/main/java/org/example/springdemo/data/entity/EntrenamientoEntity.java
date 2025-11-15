package org.example.springdemo.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntrenamientoEntity {
    private int id;
    private int usuarioId;
    private String nombre;
    private String descripcion;


    public EntrenamientoEntity() {}



}
