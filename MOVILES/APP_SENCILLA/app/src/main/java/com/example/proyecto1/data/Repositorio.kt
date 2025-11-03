package com.example.proyecto1.data

import com.example.proyecto1.domain.model.Pedido
import kotlin.text.set

object Repositorio {
private val pedidos = mutableListOf<Pedido>()
    init {
        pedidos.add(Pedido(1,"luciapeñafiel","djadjs@ajdk.com","si","4337487","nike","S"))
        pedidos.add(Pedido(2,"neyklk","siok@ajdk.com","NO QUIERO NA","646612","adidas","M"))
        pedidos.add(Pedido(3,"pimpam","tacataca@gmail.com","si","646612","puma","L"))
        pedidos.add(Pedido(4,"pepito","yesyeysy@jdakjd.cd","","646612","north face","M"))
    }
    fun getPedidos(): List<Pedido> = pedidos.toList()
    fun getPedido(id:Int): Pedido? = pedidos.getOrNull(id)
    fun addPedido(pedido: Pedido): Boolean {
        return pedidos.add(pedido)
    }

    fun actPedido(pedido: Pedido): Boolean {
        val id = pedidos.indexOfFirst { it.id == pedido.id }
        return if (id != -1) {
            pedidos[id] = pedido
            true
        } else false
    }

    fun borrarPedido(pedido: Pedido) = pedidos.remove(pedido)

    fun totalPedidos(): Int = pedidos.size
}