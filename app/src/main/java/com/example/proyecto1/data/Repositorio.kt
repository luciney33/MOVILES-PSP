package com.example.proyecto1.data

import com.example.proyecto1.domain.model.Pedido
import kotlin.text.set

object Repositorio {
private val pedidos = mutableListOf<Pedido>()
    init {
        pedidos.add(Pedido(1,"luciapeñafiel","djadjs@ajdk.com","si","4337487","nike","S"))
        pedidos.add(Pedido(2,"neyklk","siok@ajdk.com","NO QUIERO NA","646612","adidas","M"))

    }
    fun getPedidos(): List<Pedido> = pedidos.toList()
    fun getPedido(id:Int): Pedido? = pedidos.getOrNull(id)
    fun addPedido(pedido: Pedido): Boolean {
        return pedidos.add(pedido)
    }

    fun actPedido(pedido: Pedido): Boolean {
        val index = pedidos.indexOfFirst { it.id == pedido.id }
        return if (index != -1) {
            pedidos[index] = pedido
            true
        } else false
    }

    fun borrarPedido(pedido: Pedido) = pedidos.remove(pedido)

    fun totalPedidos(): Int = pedidos.size
}