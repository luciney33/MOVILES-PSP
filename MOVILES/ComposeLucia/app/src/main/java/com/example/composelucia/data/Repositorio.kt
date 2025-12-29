package com.example.composelucia.data

import com.example.composelucia.domain.model.Pedido

object Repositorio {
private val pedidos = mutableListOf<Pedido>()
    init {
        pedidos.add(Pedido("luciapeñafiel","djadjs@ajdk.com","si","4337487","nike","S"))
        pedidos.add(Pedido("neyklk","siok@ajdk.com","NO QUIERO NA","4337487","nike","M"))

    }
    fun getPedido(id:Int) = pedidos[id]
    fun addPedido(pedido: Pedido) =pedidos.add(pedido)

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