package com.example.appcomposelucia.domain.usecases

import com.example.appcomposelucia.data.Repositorio
import javax.inject.Inject

class BorrarPedidoUseCase @Inject constructor() {
    operator fun invoke(id: Int): Boolean{
        return Repositorio.borrarPedido(id)
    }
}