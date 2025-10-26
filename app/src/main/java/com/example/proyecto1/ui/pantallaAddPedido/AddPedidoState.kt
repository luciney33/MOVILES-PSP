package com.example.proyecto1.ui.pantallaAddPedido

import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.ui.common.UiEvent

data class AddPedidoState (
    val pedido: Pedido? = Pedido(),
    val mensaje: String? = null,
    val idPedido: Int = 0,
    val uiEvent: UiEvent? = null
)
