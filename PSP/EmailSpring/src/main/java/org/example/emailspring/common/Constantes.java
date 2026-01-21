package org.example.emailspring.common;

public final class Constantes {
    public static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    public static final String NOM_APP_GOOGLEAUTHTENTICATOR = "EmailSpring con Autenticacion de dos factores";
    public static final String ERROR_GENERANDO_CODIGO_QR = "Error generando código QR: ";
    public static final String NO_HAY_UN_PROCESO_DE_HABILITACION_2_FA_PENDIENTE = "No hay un proceso de habilitación 2FA pendiente";
    public static final String CODIGO_INVALIDO_VERIFICA_QUE_TU_APP_ESTE_SINCRONIZADA_CORRECTAMENTE = "Código inválido. Verifica que tu app esté sincronizada correctamente.";
    public static final String NO_HAY_UN_LOGIN_PENDIENTE_DE_VERIFICACION_2_FA = "No hay un login pendiente de verificación 2FA";
    public static final String EL_USUARIO_NO_TIENE_2_FA_HABILITADO = "El usuario no tiene 2FA habilitado";
    public static final String CODIGO_DE_VERIFICACION_INVALIDO = "Código de verificación inválido";
    public static final String AUTENTICA_A_UN_USUARIO_SI_TIENE_2_FA_ACTIVADO_RETORNA_REQUIRES_TWO_FACTOR_TRUE = "Autentica a un usuario. Si tiene 2FA activado, retorna requiresTwoFactor=true.";
    public static final String LOGIN_EXITOSO_O_SE_REQUIERE_CODIGO_2_FA = "Login exitoso o se requiere código 2FA";
    public static final String HABILITAR_2_FA_PASO_1_GENERAR_QR = "Habilitar 2FA - Paso 1: Generar QR";
    public static final String GENERA_UN_SECRETO_TOTP_Y_DEVUELVE_EL_QR_CODE_PARA_ESCANEAR_CON_GOOGLE_AUTHENTICATOR = "Genera un secreto TOTP y devuelve el QR code para escanear con Google Authenticator";
    public static final String SECRETO_Y_QR_CODE_GENERADOS = "Secreto y QR code generados";
    public static final String HABILITAR_2_FA_PASO_2_CONFIRMAR_CODIGO = "Habilitar 2FA - Paso 2: Confirmar código";
    public static final String VERIFICA_EL_CODIGO_TOTP_GENERADO_POR_LA_APP_AUTENTICADORA_Y_ACTIVA_2_FA_PERMANENTEMENTE = "Verifica el código TOTP generado por la app autenticadora y activa 2FA permanentemente";
    public static final String FA_ACTIVADO_EXITOSAMENTE = "2FA activado exitosamente";

    private Constantes() {}
    public static final String CODIGO = "codigo";

    public static final String ACTIVAR = "/activar";
    public static final String NOMBRE_USUARIO = "nombreUsuario";
    public static final String MENSAJE_ERROR = "mensajeError";
    public static final String TEMPLATE = "usuario-activo";
    public static final String TEMPLATE_ERROR = "error-activacion";
    public static final String API = "/api/**";
    public static final String AUTH_PUBLIC_LOGIN = "/api/auth/login";
    public static final String AUTH_PUBLIC_REGISTER = "/api/auth/register";
    public static final String AUTH_PUBLIC_ACTIVAR = "/api/auth/activar";
    public static final String AUTH_PUBLIC_LOGOUT = "/api/auth/logout";
    public static final String AUTH_PUBLIC_VERIFY_2FA = "/api/auth/verify-2fa";
    public static final String SWAGGER_UI = "/swagger-ui/**";

    public static final String SWAGGER_DOCS = "/v3/api-docs/**";
    public static final String WEBJARS = "/webjars/**";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final String UTF_8 = "UTF-8";
    public static final String ACTIVACION_DE_CUENTA_SISTEMA = "Activación de cuenta - Sistema";
    public static final String ERROR_AL_ENVIAR_CORREO_A = "Error al enviar correo a {}";
    public static final String ERROR_AL_ENVIAR_CORREO = "Error al enviar correo: ";
    public static final String CODIGO_ACTIVACION = "codigoActivacion";
    public static final String EMAIL_ACTIVACION = "email-activacion";
    public static final String ROL_ASIGNADO_AL_USUARIO_OPCIONAL_SI_EL_REGISTRO_PERMITE_ELEGIR_ROL = "Rol asignado al usuario (opcional, si el registro permite elegir rol).";
    public static final String USER = "USER";
    public static final String ALL = "*";

    public static final String ROL = "rol";

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
    public static final String MSG_USER_NOT_AUTHENTICATED = "No autenticado";
    public static final String NO_ENCONTRADO = "Entrenamiento no encontrado";
    public static final String SESSION_USUARIO_ID = "usuario_id";

    public static final String MSG_USERNAME_YA_EN_USO = "El nombre de usuario ya está en uso.";
    public static final String MSG_EMAIL_YA_EN_USO = "El correo electrónico ya está en uso.";
    public static final String SWAGGER_API_TITLE = "Email Spring API";
    public static final String SWAGGER_API_VERSION = "1.0";
    public static final String SWAGGER_API_DESCRIPTION = "API para gestión de entrenamientos y autenticación";
    public static final String SWAGGER_CONTACT_NAME = "Lucia";

    public static final String CODIGO_INVALIDO = "Código inválido";
    public static final String TAG_AUTENTICACION = "Autenticación";
    public static final String TAG_AUTENTICACION_DESC = "Operaciones de registro, login y logout de usuarios.";
    public static final String TAG_ACTIVACION_CUENTA = "Activación de Cuenta";
    public static final String TAG_ACTIVACION_CUENTA_DESC = "Endpoints para la activación de cuentas de usuario por correo.";
    public static final String TAG_ENTRENAMIENTOS = "Entrenamientos";
    public static final String TAG_ENTRENAMIENTOS_DESC = "Gestión de entrenamientos y rutinas (requiere autenticación).";

    public static final String OP_INICIAR_SESION = "Iniciar sesión";
    public static final String OP_CERRAR_SESION = "Cerrar sesión";
    public static final String OP_CERRAR_SESION_DESC = "Invalida la sesión actual del usuario.";
    public static final String OP_REGISTRAR_USUARIO = "Registrar nuevo usuario";
    public static final String OP_REGISTRAR_USUARIO_DESC = "Crea una nueva cuenta de usuario y envía correo de activación.";
    public static final String OP_ACTIVAR_CUENTA = "Activar cuenta de usuario";
    public static final String OP_ACTIVAR_CUENTA_DESC = "Valida un código de activación y activa la cuenta del usuario.";
    public static final String OP_LISTAR_ENTRENAMIENTOS = "Listar todos los entrenamientos";

    public static final String SCHEMA_ENTRENAMIENTO = "Representa una rutina de entrenamiento asignada a un usuario.";
    public static final String SCHEMA_ENTRENAMIENTO_ID = "Identificador único del entrenamiento";
    public static final String SCHEMA_ENTRENAMIENTO_ID_EXAMPLE = "1";
    public static final String SCHEMA_ENTRENAMIENTO_USUARIO_ID = "ID del usuario propietario del entrenamiento";
    public static final String SCHEMA_ENTRENAMIENTO_USUARIO_ID_EXAMPLE = "101";
    public static final String SCHEMA_ENTRENAMIENTO_NOMBRE = "Nombre del entrenamiento";
    public static final String SCHEMA_ENTRENAMIENTO_NOMBRE_EXAMPLE = "Rutina de Piernas Avanzada";
    public static final String SCHEMA_ENTRENAMIENTO_DESCRIPCION = "Descripción detallada de la rutina y ejercicios";
    public static final String SCHEMA_ENTRENAMIENTO_DESCRIPCION_EXAMPLE = "Incluye sentadillas, peso muerto y zancadas.";

    public static final String SCHEMA_USUARIO = "Detalles completos de un usuario del sistema.";
    public static final String SCHEMA_USUARIO_ID = "Identificador único del usuario";
    public static final String SCHEMA_USUARIO_ID_EXAMPLE = "1";
    public static final String SCHEMA_USUARIO_USERNAME = "Nombre de usuario (login)";
    public static final String SCHEMA_USUARIO_USERNAME_EXAMPLE = "jane_doe";
    public static final String SCHEMA_USUARIO_PASSWORD = "Contraseña (solo escritura, no se muestra en respuestas)";
    public static final String SCHEMA_USUARIO_EMAIL = "Correo electrónico del usuario";
    public static final String SCHEMA_USUARIO_EMAIL_EXAMPLE = "jane@example.com";
    public static final String SCHEMA_USUARIO_NOMBRE = "Nombre completo del usuario";
    public static final String SCHEMA_USUARIO_NOMBRE_EXAMPLE = "Jane Doe";
    public static final String SCHEMA_USUARIO_ROL = "Rol del usuario (ADMIN o USER)";
    public static final String SCHEMA_USUARIO_ROL_EXAMPLE = "USER";
    public static final String SCHEMA_USUARIO_ACTIVO = "Estado de activación de la cuenta";
    public static final String SCHEMA_USUARIO_ACTIVO_EXAMPLE = "true";
    public static final String SCHEMA_USUARIO_CODIGO_ACTIVACION = "Código de activación (oculto en respuestas públicas)";
    public static final String SCHEMA_USUARIO_EXPIRACION_CODIGO = "Fecha/hora de expiración del código de activación";
    public static final String OP_LISTAR_ENTRENAMIENTOS_DESC = "Permite a usuarios autenticados ver todos los entrenamientos disponibles.";
    public static final String OP_OBTENER_ENTRENAMIENTO = "Obtener entrenamiento por ID";
    public static final String OP_OBTENER_ENTRENAMIENTO_DESC = "Recupera un entrenamiento específico. Solo accesible para administradores.";
    public static final String OP_CREAR_ENTRENAMIENTO = "Crear un nuevo entrenamiento";
    public static final String OP_CREAR_ENTRENAMIENTO_DESC = "Crea un nuevo registro de entrenamiento. Solo accesible para administradores.";
    public static final String OP_ACTUALIZAR_ENTRENAMIENTO = "Actualizar entrenamiento existente";
    public static final String OP_ACTUALIZAR_ENTRENAMIENTO_DESC = "Actualiza los detalles de un entrenamiento por su ID. Solo accesible para administradores.";
    public static final String OP_ELIMINAR_ENTRENAMIENTO = "Eliminar entrenamiento";
    public static final String OP_ELIMINAR_ENTRENAMIENTO_DESC = "Elimina un entrenamiento por su ID. Solo accesible para administradores.";

    public static final String RESP_USUARIO_REGISTRADO_EXITOSAMENTE = "Usuario registrado exitosamente";
    public static final String RESP_LISTA_ENTRENAMIENTOS_RECUPERADA = "Lista de entrenamientos recuperada con éxito";
    public static final String RESP_NO_AUTORIZADO = "No autorizado";
    public static final String RESP_ENTRENAMIENTO_ENCONTRADO = "Entrenamiento encontrado";
    public static final String RESP_ACCESO_DENEGADO_NO_ADMIN = "Acceso denegado (no es admin)";
    public static final String RESP_ENTRENAMIENTO_CREADO = "Entrenamiento creado con éxito";
    public static final String RESP_DATOS_INVALIDOS = "Datos de entrada inválidos";
    public static final String RESP_ENTRENAMIENTO_ACTUALIZADO = "Entrenamiento actualizado";
    public static final String RESP_ENTRENAMIENTO_ELIMINADO = "Entrenamiento eliminado (sin contenido de respuesta)";

    public static final String SCHEMA_USUARIO_DTO_DESC = "Datos necesarios para registrar un nuevo usuario en el sistema.";
    public static final String SCHEMA_USERNAME_DESC = "Nombre de usuario único.";
    public static final String SCHEMA_USERNAME_EXAMPLE = "nuevo_usuario";
    public static final String SCHEMA_PASSWORD_DESC = "Contraseña segura para la cuenta.";
    public static final String SCHEMA_PASSWORD_EXAMPLE = "MiPasswordFuerte123";
    public static final String SCHEMA_EMAIL_DESC = "Dirección de correo electrónico válida.";
    public static final String SCHEMA_EMAIL_EXAMPLE = "correo@ejemplo.com";
    public static final String SCHEMA_NOMBRE_DESC = "Nombre completo o apodo del usuario.";
    public static final String SCHEMA_NOMBRE_EXAMPLE = "Nuevo Usuario Demo";

    public static final String SCHEMA_LOGIN_REQUEST_DESC = "Petición de credenciales para iniciar sesión.";
    public static final String SCHEMA_LOGIN_USERNAME_DESC = "Nombre de usuario o dirección de correo electrónico.";
    public static final String SCHEMA_LOGIN_USERNAME_EXAMPLE = "juan_perez";
    public static final String SCHEMA_LOGIN_PASSWORD_DESC = "Contraseña del usuario.";
    public static final String SCHEMA_LOGIN_PASSWORD_EXAMPLE = "unaContraseñaSegura123";

    public static final String SCHEMA_LOGIN_RESPONSE_DESC = "Respuesta devuelta tras un intento de login exitoso.";
    public static final String SCHEMA_LOGIN_SUCCESS_DESC = "Indica si la operación fue exitosa (siempre true en esta respuesta).";
    public static final String SCHEMA_LOGIN_SUCCESS_EXAMPLE = "true";
    public static final String SCHEMA_LOGIN_MESSAGE_DESC = "Mensaje descriptivo del resultado.";
    public static final String SCHEMA_LOGIN_MESSAGE_EXAMPLE = "Login exitoso";
    public static final String SCHEMA_LOGIN_USUARIO_DESC = "Detalles del usuario autenticado.";

    public static final String SCHEMA_USUARIO_RESPONSE_DESC = "Representación pública y segura de los datos de un usuario.";
    public static final String SCHEMA_ID_DESC = "ID del usuario.";
    public static final String SCHEMA_ID_EXAMPLE = "1";
    public static final String SCHEMA_USERNAME_RESPONSE_EXAMPLE = "juan_perez";
    public static final String SCHEMA_EMAIL_RESPONSE_EXAMPLE = "juan@ejemplo.com";
    public static final String SCHEMA_NOMBRE_RESPONSE_DESC = "Nombre visible del usuario.";
    public static final String SCHEMA_NOMBRE_RESPONSE_EXAMPLE = "Juan Perez";
    public static final String SCHEMA_ROL_DESC = "Rol del usuario en el sistema.";
    public static final String SCHEMA_ROL_EXAMPLE = "USER";

    public static final String PARAM_CODIGO_ACTIVACION_DESC = "Código de activación enviado por correo";

    public static final String SECURITY_SESSION_COOKIE_AUTH = "sessionCookieAuth";

    public static final String HTTP_200 = "200";
    public static final String HTTP_201 = "201";
    public static final String HTTP_204 = "204";
    public static final String HTTP_400 = "400";
    public static final String HTTP_401 = "401";
    public static final String HTTP_403 = "403";
    public static final String HTTP_404 = "404";
    public static final String LOGIN_COMPLETADO_EXITOSAMENTE = "Login completado exitosamente";

    public static final String EMAIL_2FA = "email-2fa";
    public static final String CODIGO_VERIFICACION_2FA = "Código de verificación 2FA - EmailSpring";
    public static final String PENDING_2FA_USERNAME = "pending2FAUsername";
    public static final String PENDING_2FA_SECRET = "pending2FASecret";
    public static final String MSG_2FA_REQUERIDO = "Se requiere código de autenticación de dos factores";
    public static final String AUTH_2FA_ENABLE = "/2fa/enable";
    public static final String AUTH_2FA_CONFIRM = "/2fa/confirm";
    public static final String AUTH_2FA_DISABLE = "/2fa/disable";
    public static final String AUTH_2FA_VERIFY = "/2fa/verify";
    public static final String AUTH_2FA_STATUS = "/2fa/status";
    public static final String MSG_2FA_ACTIVADA = "Autenticación de dos factores activada correctamente";
    public static final String MSG_2FA_DESACTIVADA = "Autenticación de dos factores desactivada";
    public static final String MSG_ESCANEA_QR = "Escanea el código QR con tu aplicación autenticadora (Google Authenticator, Authy, etc.) y confirma con un código";

    public static final String OP_DESACTIVAR_2FA = "Desactivar 2FA";
    public static final String OP_DESACTIVAR_2FA_DESC = "Desactiva la autenticación de dos factores para el usuario actual";
    public static final String RESP_2FA_DESACTIVADO = "2FA desactivado";
    public static final String OP_LOGIN_PASO_2_VERIFICAR_CODIGO_TOTP = "Login - Paso 2: Verificar código TOTP";
    public static final String OP_LOGIN_PASO_2_DESC = "Completa el login verificando el código TOTP de Google Authenticator";
    public static final String RESP_CODIGO_VERIFICADO_LOGIN_COMPLETADO = "Código verificado, login completado";
    public static final String RESP_CODIGO_INVALIDO_O_EXPIRADO = "Código inválido o expirado";
    public static final String OP_OBTENER_ESTADO_2FA = "Obtener estado del 2FA";
    public static final String OP_OBTENER_ESTADO_2FA_DESC = "Consulta si el usuario tiene activada la autenticación de dos factores";
    public static final String RESP_ESTADO_2FA_OBTENIDO = "Estado del 2FA obtenido";
}
