package com.example.appcomposelucia.domain.usecases

import com.example.appcomposelucia.data.Repositorio
import com.example.appcomposelucia.domain.model.Pedido
import javax.inject.Inject

class VerPedidoUseCase @Inject constructor() {
    operator fun invoke(id: Int): Pedido = Repositorio.getPedido(id)
}