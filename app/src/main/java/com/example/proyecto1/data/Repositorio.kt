package com.example.proyecto1.data

import com.example.proyecto1.domain.model.Pedido

object Repositorio {
private val pedidos = mutableListOf<Pedido>()
    init {
        pedidos.add(Pedido("luciapeñafiel","djadjs@ajdk.com","si","4337487","nike","S"))
        pedidos.add(Pedido("neyklk","siok@ajdk.com","NO QUIERO NA","646612","adidas","M"))

    }
    fun getPedidos(): List<Pedido> = pedidos.toList()
    fun getPedido(id:Int): Pedido? = pedidos.getOrNull(id)
    fun addPedido(pedido: Pedido): Int{
        pedidos.add(pedido)
        return pedidos.size -1
    }

    fun actPedido(id: Int, pedido: Pedido): Boolean {
        return if (id in pedidos.indices){
            pedidos[id] = pedido
            true
        }else false
    }

    fun borrarPedido(id: Int): Boolean{
        return if (id in pedidos.indices){
            pedidos.removeAt(id)
            true
        }else false
    }
    fun totalPedidos(): Int = pedidos.size
}