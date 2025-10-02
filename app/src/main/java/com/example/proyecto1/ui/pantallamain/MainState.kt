package com.example.proyecto1.ui.pantallamain

import java.sql.Date
import java.time.LocalDate

data class MainState (
    val stateNombre: String= "",
    val stateApellido: String = "",
    val stateCorreo: String = "",
    val stateComentario: String = "",
    val stateTelf: Int = 0,
    val stateFechaNac: LocalDate? = null,
    val stateSexo: String= ""
)
