package com.example.proyecto1.domain.usecases

import com.example.proyecto1.data.Repositorio

class BorrarPedidoUseCase {
    operator fun invoke(id: Int): Boolean{
        return Repositorio.borrarPedido(id)
    }
}