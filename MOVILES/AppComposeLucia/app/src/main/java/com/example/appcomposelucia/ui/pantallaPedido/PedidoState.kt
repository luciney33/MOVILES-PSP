package com.example.appcomposelucia.ui.pantallaPedido

import com.example.appcomposelucia.domain.model.Pedido

data class PedidoState(
    val indiceActual: Int = -1,
    val pedidoActual: Pedido = Pedido(),
    val totalPedidos: Int = 0,
    val mensaje: String? = null
)
