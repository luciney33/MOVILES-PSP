package com.example.proyecto1.domain.usecases

import com.example.proyecto1.data.Repositorio
import com.example.proyecto1.domain.model.Pedido
import kotlin.random.Random

class GetPedidosUseCase {
    operator fun invoke(): List<Pedido> = Repositorio.getPedidos()
}