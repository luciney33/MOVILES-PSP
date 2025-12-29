package com.example.appcomposelucia.domain.usecases

import com.example.appcomposelucia.data.Repositorio
import com.example.appcomposelucia.domain.model.Pedido

class AddPedidoUseCase {

    operator fun invoke(pedido: Pedido): Boolean{
        return Repositorio.addPedido(pedido)
    }
}