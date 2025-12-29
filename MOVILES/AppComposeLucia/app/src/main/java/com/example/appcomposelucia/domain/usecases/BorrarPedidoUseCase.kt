package com.example.appcomposelucia.domain.usecases

import com.example.appcomposelucia.data.Repositorio

class BorrarPedidoUseCase {
    operator fun invoke(id: Int): Boolean{
        return Repositorio.borrarPedido(id)
    }
}