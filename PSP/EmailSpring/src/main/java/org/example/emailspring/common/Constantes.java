package org.example.emailspring.common;

public final class Constantes {
    public static final String CODIGO = "codigo";
    public static final String ACTIVAR = "/activar";

    public static final String NOMBRE_USUARIO = "nombreUsuario";
    public static final String MENSAJE_ERROR = "mensajeError";
    public static final String TEMPLATE = "usuario-activo";
    public static final String TEMPLATE_ERROR = "error-activacion";
    public static final String API = "/api/**";
    public static final String EXCLUDE_URL = "/api/auth/**";
    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String CLASSPATH_SWAGGER = "classpath:/META-INF/resources/webjars/springdoc-openapi-ui/";
    public static final String WEBJARS = "/webjars/**";
    public static final String CLASSPATH_WEBJARS = "classpath:/META-INF/resources/webjars/";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String SWAGGER_UI_INDEX_HTML = "/swagger-ui/index.html";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final String UTF_8 = "UTF-8";
    public static final String ACTIVACIÓN_DE_CUENTA_SISTEMA = "Activación de cuenta - Sistema";
    public static final String ERROR_AL_ENVIAR_CORREO_A = "Error al enviar correo a {}";
    public static final String ERROR_AL_ENVIAR_CORREO = "Error al enviar correo: ";
    public static final String CODIGO_ACTIVACION = "codigoActivacion";
    public static final String EMAIL_ACTIVACION = "email-activacion";


    private Constantes() {}


    public static final String API_AUTH = "/api/auth";
    public static final String API_ENTRENAMIENTOS = "/api/entrenamientos";
    public static final String TABLE_ENTRENAMIENTO= "entrenamientos";
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String API_ACTIVAR = "/api/auth/activar";



    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_LOGOUT = "/logout";
    public static final String AUTH_REGISTER= "/register";

    public static final String CODIGO_DE_ACTIVACION_INVALIDO= "Código de activación inválido.";
    public static final String EXPIRADO_CODIGO_DE_ACTIVACION = "El código de activación ha expirado.";

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
    public static final String NO_ENCONTRADO = "Entrenamiento no encontrado";


    public static final String MSG_CANNOT_DELETE_USER = "No se puede eliminar el usuario con id %d porque tiene referencias existentes.";

    public static final String SESSION_USUARIO_ID = "usuarioId";
    public static final String SESSION_ATTR_USUARIO = "usuario";

}
