package org.example.springdemo.data.utilities;

public final class Queries {
    private Queries() {}

    public static final String SELECT_FROM_EJERCICIO = "SELECT * FROM ejercicio";
    public static final String SELECT_EJERCICIO_BY_ID = "SELECT * FROM ejercicio WHERE id = ?";
    public static final String SELECT_EJERCICIO_BY_ENTRENAMIENTO_ID = "SELECT * FROM ejercicio WHERE entrenamientoId = ?";
    public static final String INSERT_EJERCICIO = "INSERT INTO ejercicio(entrenamientoId, nombre, repeticiones, series) VALUES (?,?,?,?)";
    public static final String UPDATE_EJERCICIO = "UPDATE ejercicio SET entrenamientoId=?, nombre=?, repeticiones=?, series=? WHERE id=? ";
    public static final String DELETE_EJERCICIO_BY_ENTRENAMIENTO_ID = "DELETE FROM ejercicio WHERE entrenamientoId=?";
    public static final String DELETE_EJERCICIO_BY_ID = "DELETE FROM ejercicio WHERE id=?";

    public static final String SELECT_FROM_ENTRENAMIENTO = "SELECT * FROM entrenamiento";
    public static final String SELECT_ENTRENAMIENTO_BY_ID = "SELECT * FROM entrenamiento WHERE id = ?";
    public static final String SELECT_ENTRENAMIENTO_BY_USUARIO_ID = "SELECT * FROM entrenamiento WHERE usuarioId = ?";
    public static final String INSERT_ENTRENAMIENTO = "INSERT INTO entrenamiento (usuarioId, nombre, descripcion) VALUES (?, ?, ?)";
    public static final String UPDATE_ENTRENAMIENTO = "UPDATE entrenamiento SET usuarioId = ?, nombre = ?, descripcion = ? WHERE id = ?";
    public static final String DELETE_ENTRENAMIENTO_BY_USUARIO_ID = "DELETE FROM entrenamiento WHERE usuarioId = ?";
    public static final String DELETE_ENTRENAMIENTO_BY_ID = "DELETE FROM entrenamiento WHERE id = ?";

    public static final String SELECT_FROM_USUARIO = "SELECT * FROM usuario";
    public static final String SELECT_USUARIO_BY_ID = "SELECT * FROM usuario WHERE id = ?";
    public static final String SELECT_USUARIO_BY_USERNAME = "SELECT * FROM usuario WHERE username = ?";
    public static final String INSERT_USUARIO = "INSERT INTO usuario(username,password,email,nombre,rol) VALUES(?,?,?,?,?)";
    public static final String UPDATE_USUARIO = "UPDATE usuario SET username=?, password=?, email=?, nombre=?, rol=? WHERE id=?";
    public static final String DELETE_USUARIO_BY_ID = "DELETE FROM usuario WHERE id=?";
}

