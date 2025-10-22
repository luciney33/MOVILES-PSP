package com.example.proyecto1.ui.pantallaDetallePedido

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.BorrarPedidoUseCase
import com.example.proyecto1.domain.usecases.GetPedidosUseCase
import com.example.proyecto1.ui.common.Constantes
import com.example.proyecto1.ui.common.UiEvent
import kotlin.compareTo


class DetallePedidoViewModel(
    private val actPedidoUseCase: ActPedidoUseCase,
    private val borrarPedidoUseCase: BorrarPedidoUseCase,
    private val getPedidosUseCase: GetPedidosUseCase
) : ViewModel(){

    private var indice = 0
    private val _uiState: MutableLiveData<DetallePedidoState> = MutableLiveData(null)
    val uiState: LiveData<DetallePedidoState> get() = _uiState

    fun getPedidos(id: Int){
        val pedidos = getPedidosUseCase()

        val pedido = pedidos.find { it.id == id }

        if (pedido == null) {
            if (pedidos.size < id || id < 0) {
                _uiState.value =
                    _uiState.value?.copy(event = UiEvent.ShowSnackbar(Constantes.ERROR))
            }
            else
                _uiState.value =
                    _uiState.value?.copy(event = UiEvent.ShowSnackbar(Constantes.ERROR))

        } else

            _uiState.value = _uiState.value?.copy(pedido = pedido) ?: DetallePedidoState(pedido)

    }
    class DetallePedidoViewModelFactory(
        private val actPedidoUseCase: ActPedidoUseCase = ActPedidoUseCase(),
        private val borrarPedidoUseCase: BorrarPedidoUseCase = BorrarPedidoUseCase(),
        private val getPedidosUseCase: GetPedidosUseCase = GetPedidosUseCase()
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetallePedidoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetallePedidoViewModel(
                    actPedidoUseCase,
                    borrarPedidoUseCase,
                    getPedidosUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


