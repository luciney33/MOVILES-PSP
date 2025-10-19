package com.example.proyecto1.ui.pantallaListadoPedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.AddPedidoUseCase
import com.example.proyecto1.domain.usecases.BorrarPedidoUseCase
import com.example.proyecto1.domain.usecases.TotalPedUseCase
import com.example.proyecto1.domain.usecases.VerPedidoUseCase
import com.example.proyecto1.ui.pantallaAddPedido.AddPedidoViewModel

class ListadoPedidosViewModel {


}
    class ListadoViewModelFactory(
        private val addPedidoUseCase: AddPedidoUseCase = AddPedidoUseCase(),
        private val verPedidoUseCase: VerPedidoUseCase = VerPedidoUseCase(),
        private val actPedidoUseCase: ActPedidoUseCase = ActPedidoUseCase(),
        private val borrarPedidoUseCase: BorrarPedidoUseCase = BorrarPedidoUseCase(),
        private val totalPedUseCase: TotalPedUseCase = TotalPedUseCase()
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddPedidoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddPedidoViewModel(
                    addPedidoUseCase,
                    verPedidoUseCase,
                    actPedidoUseCase,
                    borrarPedidoUseCase,
                    totalPedUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}