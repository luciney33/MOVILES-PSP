package com.example.appcomposelucia.domain.usecases

import com.example.appcomposelucia.data.Repositorio
import com.example.appcomposelucia.domain.model.Pedido

class ActPedidoUseCase {
    operator fun invoke(id: Int, pedido: Pedido) : Boolean {
        return Repositorio.actPedido(id,pedido)
    }
}