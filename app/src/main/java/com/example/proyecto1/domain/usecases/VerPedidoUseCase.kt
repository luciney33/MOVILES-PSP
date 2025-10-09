package com.example.proyecto1.domain.usecases

import com.example.proyecto1.data.Repositorio
import com.example.proyecto1.domain.model.Pedido

class VerPedidoUseCase {
    operator fun invoke(id: Int): Pedido = Repositorio.getPedido(id)
}