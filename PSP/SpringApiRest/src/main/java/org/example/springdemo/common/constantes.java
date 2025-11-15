package org.example.springdemo.common;

public final class constantes {
    private constantes() {}

    public static final String API_AUTH = "/api/auth";
    public static final String API_ENTRENAMIENTOS = "/api/entrenamientos";
    public static final String API_EJERCICIOS = "/api/ejercicios";

    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_LOGOUT = "/logout";
    public static final String AUTH_SESSION = "/session";

    public static final String PATH_ID = "/{id}";

    public static final String MSG_LOGIN_SUCCESS = "Login exitoso";
    public static final String MSG_LOGIN_INVALID = "Credenciales inválidas";
    public static final String MSG_LOGOUT_SUCCESS = "Logout exitoso";
    public static final String MSG_USER_AUTHENTICATED = "Usuario autenticado";
    public static final String MSG_USER_NOT_AUTHENTICATED = "No autenticado";

    public static final String MSG_NO_PERM_CREAR_ENTRENAMIENTO = "No tiene permisos para crear entrenamiento";
    public static final String MSG_NO_PERM_ACTUALIZAR_ENTRENAMIENTO = "No tiene permisos para actualizar entrenamiento";
    public static final String MSG_NO_PERM_CREAR_EJERCICIO = "No tiene permisos para crear ejercicio";
    public static final String MSG_NO_PERM_ACTUALIZAR_EJERCICIO = "No tiene permisos para actualizar ejercicio";

    public static final String MSG_CANNOT_DELETE_USER = "No se puede eliminar el usuario con id %d porque tiene referencias existentes.";

    public static final String SESSION_USUARIO_ID = "usuarioId";
}
