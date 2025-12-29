package com.example.appcomposelucia.ui.pantallaPedido

import com.example.appcomposelucia.domain.model.Pedido

data class PedidoState(
    val pedidos: List<Pedido> = emptyList(),
    val indiceActual: Int = -1,
    val pedidoActual: Pedido = Pedido()
)
