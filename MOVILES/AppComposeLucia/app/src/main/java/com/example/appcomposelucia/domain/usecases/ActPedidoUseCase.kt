package com.example.appcomposelucia.domain.usecases

import com.example.appcomposelucia.data.Repositorio
import com.example.appcomposelucia.domain.model.Pedido
import javax.inject.Inject

class ActPedidoUseCase @Inject constructor() {
    operator fun invoke(id: Int, pedido: Pedido) : Boolean {
        return Repositorio.actPedido(id,pedido)
    }
}