package com.example.composelucia.domain.usecases

import com.example.composelucia.data.Repositorio
import com.example.composelucia.domain.model.Pedido

class ActPedidoUseCase {
    operator fun invoke(id: Int, pedido: Pedido) : Boolean {
        return Repositorio.actPedido(id,pedido)
    }
}