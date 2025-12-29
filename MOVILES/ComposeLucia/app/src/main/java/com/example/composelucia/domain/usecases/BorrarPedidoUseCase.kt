package com.example.composelucia.domain.usecases

import com.example.composelucia.data.Repositorio

class BorrarPedidoUseCase {
    operator fun invoke(id: Int): Boolean{
        return Repositorio.borrarPedido(id)
    }
}