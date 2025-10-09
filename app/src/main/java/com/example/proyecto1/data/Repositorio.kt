package com.example.proyecto1.data

import com.example.proyecto1.domain.model.Pedido

object Repositorio {
private val pedidos = mutableListOf<Pedido>()
    init {
        pedidos.add(Pedido("luciapeñafiel","djadjs@ajdk.com","si","4337487","nike","S"))
    }
    fun getPedido(id:Int) = pedidos[id]
    fun addPedido(pedido: Pedido) =pedidos.add(pedido)

    fun actPedido(id: Int, pedido: Pedido): Boolean {
        return if (id in pedidos.indices){
            pedidos[id] = pedido
            true
        }else false
    }
}