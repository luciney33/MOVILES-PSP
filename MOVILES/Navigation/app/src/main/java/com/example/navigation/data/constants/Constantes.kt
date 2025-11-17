package com.example.navigation.data.constants


object Constantes {
    const val NO_DATA: String = "-"
    const val KG_SUFFIX: String = " kg"
    const val REPS_SUFFIX: String = " reps"

    val NUMERIC_REGEX: Regex = Regex("^\\d+$")

    // UI messages (non-localized constants). Prefer resolving to strings.xml in UI layer for localization.
    const val MENSAJE_GUARDADO: String = "Guardado"
    const val MENSAJE_ERROR_GENERICO: String = "Error"
    const val MENSAJE_SESION_FINALIZADA: String = "Sesión finalizada"
    const val MENSAJE_SESION_NO_ENCONTRADA: String = "No se encontró la sesión"
    const val MENSAJE_EJERCICIO_ELIMINADO: String = "Ejercicio eliminado de la vista"
    const val MENSAJE_ERROR_CARGA_EJERCICIO: String = "No se pudo cargar el ejercicio"
    const val VALIDACION_INTRODUCE_PESO: String = "Introduce peso"
    const val VALIDACION_INTRODUCE_GRASA: String = "Introduce grasa corporal"
    const val VALIDACION_PESO_INVALIDO: String = "Peso inválido"
    const val VALIDACION_GRASA_INVALIDA: String = "Grasa inválida"

    const val MENSAJE_RELACION_ACTUALIZADA: String = "Relación actualizada"
    const val MENSAJE_ERROR_RELACION: String = "Error actualizando relación"
    const val MENSAJE_ERROR_GUARDADO: String = "Error guardando"
}
