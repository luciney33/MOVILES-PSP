package com.example.proyecto1.data

import com.example.proyecto1.domain.model.Pedido

object Repositorio {
private val pedidos = mutableListOf<Pedido>()
    fun addPedido(pedido: Pedido) =pedidos.add(pedido)
}