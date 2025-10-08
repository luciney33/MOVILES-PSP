package com.example.proyecto1.ui.pantallamain

import com.example.proyecto1.domain.model.Ropa
import java.sql.Date
import java.time.LocalDate

data class MainState (
    val ropa: Ropa = Ropa(),
    val mensaje: String = ""
)
