package com.example.proyecto1.ui.pantallamain

import com.example.proyecto1.domain.model.Pedido

data class MainState (
    val pedido: Pedido= Pedido(),
    val mensaje: String? = null,
    val idPedido: Int = 0,
    val isDisable: Boolean = false,
)
