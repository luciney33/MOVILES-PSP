package com.example.composelucia.domain.usecases

import com.example.composelucia.data.Repositorio
import com.example.composelucia.domain.model.Pedido

class VerPedidoUseCase {
    operator fun invoke(id: Int): Pedido = Repositorio.getPedido(id)
}