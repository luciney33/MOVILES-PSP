package com.example.proyecto1.domain.usecases

import com.example.proyecto1.data.Repositorio
import com.example.proyecto1.domain.model.Pedido

class BorrarPedidoUseCase {
    operator fun invoke(pedido: Pedido): Boolean{
        return Repositorio.borrarPedido(pedido)
    }
}