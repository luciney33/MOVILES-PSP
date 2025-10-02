package com.example.proyecto1.ui.pantallamain

import java.sql.Date

data class MainState (
    val stateNombre: String= "" ,
    val stateApellido: String = "",
    val correo: String = "",
    val comentario: String = "",
    val numTelf: Int = 0,
    val fechaNac: Date = "",
    val mujer: Boolean = false,
    val hombre: Boolean = false,
    val otro: Boolean = false,
)
