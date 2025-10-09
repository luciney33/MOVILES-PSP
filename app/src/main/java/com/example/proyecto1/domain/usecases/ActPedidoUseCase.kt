package com.example.proyecto1.domain.usecases

import com.example.proyecto1.data.Repositorio
import com.example.proyecto1.domain.model.Pedido

class ActPedidoUseCase {
    operator fun invoke(id: Int, pedido: Pedido) : Boolean {
        return Repositorio.actPedido(id,pedido)
    }
}