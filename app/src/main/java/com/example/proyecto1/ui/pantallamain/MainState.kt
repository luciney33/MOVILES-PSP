package com.example.proyecto1.ui.pantallamain

import com.example.proyecto1.domain.model.Pedido
import java.sql.Date
import java.time.LocalDate

data class MainState (
    val pedido: Pedido= Pedido(),
    val mensaje: String = ""
)
