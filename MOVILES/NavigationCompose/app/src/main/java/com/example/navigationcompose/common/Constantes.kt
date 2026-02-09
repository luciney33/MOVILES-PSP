package com.example.navigationcompose.common

object Constantes {
    // ========== ERRORES ==========
    const val ERROR_DEL_SERVIDOR = "Error del servidor: "
    const val ERROR_DE_CONEXION = "Error de conexión: "
    const val ERROR_DE_RED = "Error de red: "
    const val ERROR_FALLO_CONEXION = "Fallo de conexión: "
    const val ERROR_USUARIO_PASSWORD_INCORRECTOS = "Usuario o contraseña incorrectos"
    const val ERROR_USUARIO_EMAIL_EXISTEN = "El usuario o email ya existen"
    const val ERROR_NO_CARGAR_ENTRENAMIENTOS = "No se pudieron cargar los entrenamientos"
    const val ERROR_ENTRENAMIENTO_NO_ENCONTRADO = "Entrenamiento no encontrado"
    const val ERROR_BUSCAR_DETALLE = "Error al buscar detalle"
    const val ERROR_GUARDAR_SERVIDOR = "Error al guardar en el servidor"
    const val ERROR_FALLO_RED = "Fallo de red"
    const val ERROR_NO_ELIMINAR = "No se pudo eliminar"
    const val ERROR_CARGAR_EJERCICIOS = "Error al cargar lista de ejercicios"
    const val ERROR_GENERICO = "Error: "
    const val EL_PERSONAJE_VINO_VACIO = "El personaje vino vacío"
    const val ERROR_OBTENER_DETALLE = "Error al obtener detalle: "
    const val ERROR_PAGE_NUMBER = "Page number must be non-negative"
    const val ERROR_ELIMINAR_PERSONAJE = "Error al eliminar personaje: "
    const val ERROR_ACTUALIZAR_PERSONAJE = "Error al actualizar personaje: "
    const val ERROR_AGREGAR_PERSONAJE = "Error al agregar personaje: "

    // ========== MENSAJES DE ÉXITO ==========
    const val PERSONAJE_ELIMINADO_EXITOSO = "Personaje eliminado exitosamente"
    const val PERSONAJE_ACTUALIZADO_EXITOSO = "Personaje actualizado exitosamente"
    const val PERSONAJE_AGREGADO_EXITOSO = "Personaje agregado exitosamente"

    // ========== URLs API ==========
    const val URL_CHARACTERS = "characters"
    const val URL_CHARACTERS_ID = "characters/{id}"
    const val URL_PLANETS = "planets"
    const val URL_PLACEHOLDER_POST = "posts"
    const val URL_PLACEHOLDER_DELETE = "posts/{id}"
    const val URL_PLACEHOLDER_UPDATE = "posts/{id}"

    // ========== PARÁMETROS ==========
    const val ID = "id"
    const val PAGE = "page"

    // ========== RUTAS API GYM ==========
    const val API_AUTH_LOGIN = "api/auth/login"
    const val API_AUTH_REGISTER = "api/auth/register"
    const val API_ENTRENAMIENTOS = "api/entrenamientos"
    const val API_ENTRENAMIENTOS_ID = "api/entrenamientos/{id}"
    const val API_EJERCICIOS = "api/ejercicios"

    // ========== NOMBRES DE RETROFIT ==========
    const val RETROFIT_PLACEHOLDER = "PlaceholderRetrofit"
    const val RETROFIT_DBAPI = "DragonBallRetrofit"

    // ========== JSON ==========
    const val ITEMS = "items"

    // ========== SHARED PREFERENCES ==========
    const val PREFS_NAME = "prefs_seguras"
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_REFRESH_TOKEN = "refresh_token"

    // ========== TEXTOS UI - LOGIN ==========
    const val TEXT_APP_NAME = "GYM APP"
    const val TEXT_LABEL_USUARIO = "Usuario"
    const val TEXT_LABEL_PASSWORD = "Contraseña"
    const val TEXT_BUTTON_LOGIN = "ENTRAR"
    const val TEXT_NO_ACCOUNT = "¿No tienes cuenta? Regístrate aquí"
    const val TEXT_USUARIO_EJEMPLO = "UsuarioEjemplo"

    // ========== TEXTOS UI - REGISTER ==========
    const val TEXT_TITULO_REGISTRO = "Registro"
    const val TEXT_CREAR_CUENTA = "Crear Cuenta"
    const val TEXT_LABEL_EMAIL = "Email"
    const val TEXT_LABEL_NOMBRE_COMPLETO = "Nombre completo"
    const val TEXT_LABEL_CONFIRMAR_PASSWORD = "Confirmar contraseña"
    const val TEXT_BUTTON_REGISTRARSE = "REGISTRARSE"
    const val TEXT_DESCRIPCION_VOLVER = "Volver"

    // ========== TEXTOS UI - ENTRENAMIENTO ==========
    const val TEXT_NOMBRE_ENTRENAMIENTO = "Nombre Entrenamiento"
    const val TEXT_DESCRIPCION = "Descripción"
    const val TEXT_BUTTON_CREAR = "CREAR"
    const val TEXT_BUTTON_ACTUALIZAR = "ACTUALIZAR"
    const val TEXT_RUTINA_EJEMPLO = "Rutina A"
    const val TEXT_NOTAS_PROGRESION = "Notas sobre la progresión de cargas para esta semana."
    const val TEXT_EJERCICIOS_RUTINA = "Ejercicios de esta rutina"

    // ========== TEXTOS UI - LISTA ENTRENAMIENTOS ==========
    const val TEXT_EMPUJE = "Empuje (Pecho/Tríceps)"
    const val TEXT_TRACCION = "Tracción (Espalda/Bíceps)"
    const val TEXT_PIERNA = "Pierna Completa"
    const val TEXT_ENFOQUE_FUERZA = "Enfoque en fuerza"
    const val TEXT_HIPERTROFIA = "Hipertrofia"
    const val TEXT_DIA_PESADO = "Día pesado"

    // ========== URLs SERVIDOR ==========
    const val URL_BASE_EMULATOR = "http://10.0.2.2:8080"

    // ========== TEXTOS UI - REGISTER ADICIONALES ==========
    const val TEXT_YA_TIENE_CUENTA = "¿Ya tienes cuenta? Inicia sesión"
    const val TEXT_OCULTAR_PASSWORD = "Ocultar contraseña"
    const val TEXT_MOSTRAR_PASSWORD = "Mostrar contraseña"

    // ========== TEXTOS UI - DRAGON BALL ==========
    const val TEXT_PERSONAJES_DRAGON_BALL = "Personajes Dragon Ball"
    const val TEXT_RAZA = "Raza: "
    const val TEXT_KI = "Ki: "
    const val TEXT_HERO = "Hero"
    const val TEXT_VILLAIN = "Villain"
    const val TEXT_GOKU = "Goku"
    const val TEXT_FREEZER = "Freezer"
    const val TEXT_SAIYAN = "Saiyan"
    const val TEXT_FRIEZA_RACE = "Frieza Race"
    const val TEXT_KI_GOKU = "60.000.000"
    const val TEXT_KI_FREEZER = "120.000.000"
    const val URL_EXAMPLE_GOKU_GIF = "https://example.com/goku.gif"
    const val URL_EXAMPLE_FREEZER_GIF = "https://example.com/freezer.gif"
}
