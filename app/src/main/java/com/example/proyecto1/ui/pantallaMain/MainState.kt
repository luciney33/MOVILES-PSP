package com.example.proyecto1.ui.pantallaMain

import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.ui.common.UiEvent

data class MainState (
    val pedido: Pedido= Pedido(),
    val mensaje: String? = null,
    val idPedido: Int = 0,
    val totalPedidos: Int = 0,
    val isDisable: Boolean = false,
    val uiEvent: UiEvent? = null
)
