package com.example.proyecto1.ui.pantallaListado

import com.example.proyecto1.domain.model.Pedido

data class ListadoPedidosState(
    val pedidos: List<Pedido> = emptyList(),
    val mensaje: String? = null
)
