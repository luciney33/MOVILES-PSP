package com.example.proyecto1.ui.pantallaListadoPedido

import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.ui.common.UiEvent

data class ListadoPedidosState(
    val pedidos: List<Pedido> = emptyList(),
    val totalPedidos: Int = 0,
    val pedidoId: Int? = null,
    val mensaje: String? = null,
    val uiEvent: UiEvent? = null

)
