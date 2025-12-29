package com.example.composelucia.domain.usecases

import com.example.composelucia.data.Repositorio
import com.example.composelucia.domain.model.Pedido

class AddPedidoUseCase {

    operator fun invoke(pedido: Pedido): Boolean{
        return Repositorio.addPedido(pedido)
    }
}