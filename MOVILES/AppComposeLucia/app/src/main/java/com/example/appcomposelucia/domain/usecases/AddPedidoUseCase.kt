package com.example.appcomposelucia.domain.usecases

import com.example.appcomposelucia.data.Repositorio
import com.example.appcomposelucia.domain.model.Pedido
import javax.inject.Inject

class AddPedidoUseCase @Inject constructor() {

    operator fun invoke(pedido: Pedido): Boolean{
        return Repositorio.addPedido(pedido)
    }
}