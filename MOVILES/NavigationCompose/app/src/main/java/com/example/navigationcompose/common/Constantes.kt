package com.example.navigationcompose.common

object Constantes {
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
    const val ERROR_PAGE_NUMBER = "Page number must be non-negative"
    const val URL_CHARACTERS = "characters"

    const val ID = "id"
    const val PAGE = "page"

    const val API_AUTH_LOGIN = "api/auth/login"
    const val API_AUTH_REGISTER = "api/auth/register"
    const val API_ENTRENAMIENTOS = "api/entrenamientos"
    const val API_ENTRENAMIENTOS_ID = "api/entrenamientos/{id}"
    const val API_EJERCICIOS = "api/ejercicios"

    const val RETROFIT_SECRETOSAPI = "SecretosRetrofit"

    const val RETROFIT_GYMAPI = "GymRetrofit"
    const val RETROFIT_DBAPI = "DragonBallRetrofit"

    const val ITEMS = "items"

    const val PREFS_NAME = "prefs_seguras"
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_REFRESH_TOKEN = "refresh_token"

    const val TEXT_APP_NAME = "GYM APP"
    const val TEXT_LABEL_USUARIO = "Usuario"
    const val TEXT_LABEL_PASSWORD = "Contraseña"
    const val TEXT_BUTTON_LOGIN = "ENTRAR"
    const val TEXT_NO_ACCOUNT = "¿No tienes cuenta? Regístrate aquí"
    const val TEXT_USUARIO_EJEMPLO = "UsuarioEjemplo"

    const val TEXT_TITULO_REGISTRO = "Registro"
    const val TEXT_CREAR_CUENTA = "Crear Cuenta"
    const val TEXT_LABEL_EMAIL = "Email"
    const val TEXT_LABEL_NOMBRE_COMPLETO = "Nombre completo"
    const val TEXT_LABEL_CONFIRMAR_PASSWORD = "Confirmar contraseña"
    const val TEXT_BUTTON_REGISTRARSE = "REGISTRARSE"
    const val TEXT_DESCRIPCION_VOLVER = "Volver"

    const val TEXT_NOMBRE_ENTRENAMIENTO = "Nombre Entrenamiento"
    const val TEXT_DESCRIPCION = "Descripción"
    const val TEXT_BUTTON_CREAR = "CREAR"
    const val TEXT_BUTTON_ACTUALIZAR = "ACTUALIZAR"
    const val TEXT_RUTINA_EJEMPLO = "Rutina A"
    const val TEXT_NOTAS_PROGRESION = "Notas sobre la progresión de cargas para esta semana."
    const val TEXT_EJERCICIOS_RUTINA = "Ejercicios de esta rutina"

    const val TEXT_EMPUJE = "Empuje (Pecho/Tríceps)"
    const val TEXT_TRACCION = "Tracción (Espalda/Bíceps)"
    const val TEXT_PIERNA = "Pierna Completa"
    const val TEXT_ENFOQUE_FUERZA = "Enfoque en fuerza"
    const val TEXT_HIPERTROFIA = "Hipertrofia"
    const val TEXT_DIA_PESADO = "Día pesado"

    const val URL_BASE_EMULATOR = "http://10.0.2.2:8080"

    const val TEXT_YA_TIENE_CUENTA = "¿Ya tienes cuenta? Inicia sesión"
    const val TEXT_OCULTAR_PASSWORD = "Ocultar contraseña"
    const val TEXT_MOSTRAR_PASSWORD = "Mostrar contraseña"
    const val USER = "USER"
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

    const val TEXT_GYM = "Gym"
    const val TEXT_DRAGON_BALL = "DragonBall"
    const val TEXT_SALIR = "Salir"
    const val TEXT_BIENVENIDO = "EXAMEN MOVILES"
    const val TEXT_NO_SESIONES_RECIENTES = "No hay sesiones recientes"
    const val TEXT_PERFIL = "Perfil"
    const val TEXT_CONTENIDO_PANTALLA = "Contenido de la pantalla seleccionada"

    const val AUTH_HEADER = "Authorization"
    const val AUTH_BEARER_PREFIX = "Bearer "

    const val ERROR_PASSWORDS_NO_COINCIDEN = "Las contraseñas no coinciden"
    const val ERROR_PASSWORD_VACIA = "La contraseña no puede estar vacía"
    const val ERROR_DESCONOCIDO = "Error desconocido"
    const val REGISTRO_EXITOSO_MENSAJE = "Registro completado con éxito! Activa tu cuenta desde el email y logueate"

    // ==================== CRIPTOGRAFÍA ====================

    // Algoritmos
    const val RSA_ALGORITHM = "RSA"
    const val RSA_KEY_SIZE = 4096
    const val RSA_CIPHER = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    const val SIGNATURE_ALGORITHM = "SHA256withRSA"
    const val AES_ALGORITHM = "AES"
    const val AES_KEY_SIZE = 256
    const val AES_CIPHER = "AES/GCM/NoPadding"
    const val GCM_TAG_LENGTH = 128
    const val IV_SIZE = 12
    const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    const val PBKDF2_ITERATIONS = 100000
    const val SALT_SIZE = 32
    const val SHA256_ALGORITHM = "SHA-256"

    // DataStore
    const val DATASTORE_CRYPTO_NAME = "crypto_keys_secure"
    const val KEY_ENCRYPTED_PRIVATE_KEY = "encrypted_private_key"
    const val KEY_SALT = "pbkdf2_salt"
    const val KEY_IV_PRIVATE_KEY = "iv_private_key"
    const val KEY_PUBLIC_KEY = "public_key"
    const val KEY_CERTIFICADO = "certificado_servidor"

    // Mensajes de Registro con Criptografía
    const val MSG_GENERANDO_CLAVES = "Generando claves de cifrado... (puede tardar unos segundos)"
    const val MSG_CIFRANDO_CLAVE = "Cifrando clave privada..."
    const val MSG_GUARDANDO_CLAVES = "Guardando claves de forma segura..."
    const val MSG_REGISTRANDO_USUARIO = "Registrando usuario..."
    const val ERROR_PASSWORD_MINIMA = "La contraseña debe tener al menos 8 caracteres"
    const val ERROR_GENERAR_CLAVES = "Error al generar claves: "

    // ==================== SECRETOS ====================

    // Endpoints
    const val API_SECRETOS = "/api/secretos"
    const val API_SECRETOS_ID = "/api/secretos/{id}"
    const val API_SECRETOS_COMPARTIR = "/api/secretos/{id}/compartir"
    const val API_SECRETOS_REVOCAR = "/api/secretos/{secretoId}/compartir/{usuarioId}"
    const val API_USUARIOS = "/api/usuarios"
    const val API_USUARIOS_ID = "/api/usuarios/{id}"
    const val API_USUARIOS_PUBLIC_KEY = "/api/usuarios/public-key"
    const val API_CRYPTO_PUBLIC_KEY = "/api/crypto/public-key"
    const val API_AUTH_ACTIVAR = "/api/auth/activar"

    // Errores de Secretos
    const val ERROR_PASSWORD_INCORRECTA = "Contraseña incorrecta"
    const val ERROR_RECEPTOR_NO_ENCONTRADO = "Receptor no encontrado"
    const val ERROR_CERTIFICADO_INVALIDO = "Certificado del receptor no válido"
    const val ERROR_CERTIFICADO_AUTOR_INVALIDO = "Certificado del autor no válido"
    const val ERROR_CREAR_SECRETO = "Error al crear secreto: "
    const val ERROR_SECRETO_NO_ENCONTRADO = "Secreto no encontrado"
    const val ERROR_OBTENER_SECRETOS = "Error al obtener secretos: "
    const val ERROR_DESCIFRAR_SECRETO = "Error al descifrar: "
    const val ERROR_COMPARTIR_SECRETO = "Error al compartir: "
    const val ERROR_REVOCAR_ACCESO = "Error al revocar acceso: "
    const val ERROR_ELIMINAR_SECRETO = "Error al eliminar: "
    const val ERROR_OBTENER_USUARIOS = "Error al obtener usuarios: "
    const val ERROR_USUARIO_NO_ENCONTRADO = "Usuario no encontrado"
    const val ERROR_RED_GENERICO = "Error de red: "
    const val ERROR_GENERICO_MENSAJE = "Error: "
    const val ERROR_OBTENER_CLAVE_PUBLICA = "Error al obtener clave pública: "
    const val ERROR_ELIMINAR_ENTRENAMIENTO = "Error al eliminar el entrenamiento"
    const val ERROR_CARGAR_ENTRENAMIENTO = "Error al cargar el entrenamiento"
    const val ERROR_GUARDAR_ENTRENAMIENTO = "Error al guardar el entrenamiento"
    const val ERROR_NOMBRE_VACIO = "El nombre no puede estar vacío"
    const val ERROR_ENDPOINT_NO_IMPLEMENTADO = "Endpoint no implementado en el servidor:"

    // ==================== ERRORES CRIPTOGRAFÍA ====================

    const val ERROR_NO_CLAVE_PRIVADA = "No hay clave privada guardada"
    const val ERROR_NO_SALT = "No hay salt guardado"
    const val ERROR_NO_IV = "No hay IV guardado"
    const val ERROR_NO_CLAVE_PUBLICA = "No hay clave pública guardada"

    // Mensajes de Secretos
    const val MSG_NO_SESION_ACTIVA = "No hay sesión activa. Vuelve a iniciar sesión."
    const val MSG_TOKEN_EXPIRADO = "Token expirado. Vuelve a iniciar sesión."
    const val MSG_SECRETO_CREADO = "Secreto creado exitosamente"
    const val MSG_SECRETO_COMPARTIDO = "Secreto compartido exitosamente"
    const val MSG_ACCESO_REVOCADO = "Acceso revocado exitosamente"
    const val MSG_SECRETO_ELIMINADO = "Secreto eliminado exitosamente"

    // ==================== HOME SCREEN ====================

    const val TEXT_BIENVENIDO_FITNESS = "¡Bienvenido a tu aplicación de fitness!"
    const val TEXT_MENU_EXPLORAR = "Usa el menú para explorar:"
    const val TEXT_GYM_DESCRIPCION = "Gestiona tus entrenamientos"
    const val TEXT_DRAGON_BALL_DESCRIPCION = "Explora personajes"

    const val TEXT_SECRETOS = "Secretos"
    const val TEXT_MIS_SECRETOS = "Mis Secretos"
    const val TEXT_CREAR_SECRETO = "Crear Secreto"
    const val TEXT_CANCELAR = "Cancelar"
    const val TEXT_GUARDAR = "Guardar"
    const val TEXT_COMPARTIR = "Compartir"
    const val TEXT_COMPARTIR_CON = "Compartir con..."
    const val TEXT_SELECCIONAR_USUARIO = "Seleccionar Usuario"
    const val TEXT_CONTENIDO_SECRETO = "Contenido del secreto"
    const val TEXT_VER_SECRETO = "Ver Secreto"
    const val TEXT_ELIMINAR = "Eliminar"
    const val TEXT_REVOCAR_ACCESO = "Revocar Acceso"
    const val TEXT_COMPARTIDO_CON = "Compartido con:"
    const val TEXT_AUTOR = "Autor:"
    const val TEXT_FECHA_CREACION = "Fecha:"
    const val TEXT_FIRMA_VALIDA = "Firma válida ✓"
    const val TEXT_FIRMA_INVALIDA = "Firma inválida ✗"
    const val TEXT_INGRESE_PASSWORD = "Ingrese su contraseña para descifrar"
    const val TEXT_DESCIFRAR = "Descifrar"
    const val TEXT_NO_SECRETOS = "No hay secretos"
    const val TEXT_CREAR_PRIMER_SECRETO = "Crea tu primer secreto cifrado"
    const val TEXT_CONTENIDO_CIFRADO = "Contenido cifrado"
    const val TEXT_INGRESE_CONTENIDO = "Ingrese el contenido del secreto"
    const val TEXT_SECRETO_CREADO_EXITO = "Secreto creado exitosamente"
    const val TEXT_SECRETO_COMPARTIDO_EXITO = "Secreto compartido exitosamente"
    const val TEXT_SECRETO_ELIMINADO_EXITO = "Secreto eliminado exitosamente"
    const val TEXT_ACCESO_REVOCADO_EXITO = "Acceso revocado exitosamente"

    // ==================== LÍMITES RSA ====================

    const val RSA_4096_MAX_BYTES = 446
    const val ERROR_DATOS_GRANDES_RSA = "Datos demasiado grandes para RSA-4096. Máximo: 446 bytes. Usa AES para cifrar contenido y RSA solo para la clave AES."
}
