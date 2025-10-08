package com.example.proyecto1.domain.usecases

import com.example.proyecto1.data.Repositorio
import com.example.proyecto1.domain.model.Pedido

class AddPedidoUseCase {
    operator fun invoke(pedido: Pedido): Boolean{
    return Repositorio.addPedido(pedido)
    }
}