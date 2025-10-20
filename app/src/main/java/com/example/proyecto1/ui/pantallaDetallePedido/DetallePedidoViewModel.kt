package com.example.proyecto1.ui.pantallaDetallePedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.BorrarPedidoUseCase


class DetallePedidoViewModel(
    private val actPedidoUseCase: ActPedidoUseCase,
    private val borrarPedidoUseCase: BorrarPedidoUseCase
) : ViewModel(){


    class DetallePedidoViewModelFactory(
        private val actPedidoUseCase: ActPedidoUseCase = ActPedidoUseCase(),
        private val borrarPedidoUseCase: BorrarPedidoUseCase = BorrarPedidoUseCase(),
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetallePedidoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetallePedidoViewModel(
                    actPedidoUseCase,
                    borrarPedidoUseCase,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}