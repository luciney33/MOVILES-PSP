package com.example.proyecto1.ui.pantallaDetallePedido

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.R
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.BorrarPedidoUseCase
import com.example.proyecto1.domain.usecases.GetPedidosUseCase
import com.example.proyecto1.ui.common.StringProvider
import com.example.proyecto1.ui.common.UiEvent


class DetallePedidoViewModel(
    private val stringProvider: StringProvider,
    private val actPedidoUseCase: ActPedidoUseCase,
    private val borrarPedidoUseCase: BorrarPedidoUseCase,
    private val getPedidosUseCase: GetPedidosUseCase
) : ViewModel() {

    private val _state: MutableLiveData<DetallePedidoState> = MutableLiveData(null)
    val state: LiveData<DetallePedidoState> get() = _state

    fun getPedidos(id: Int) {
        val pedidos = getPedidosUseCase()
        val pedido = pedidos.find { it.id == id }
        if (pedido == null) {
            if (pedidos.size < id || id < 0) {
                _state.value =
                    _state.value?.copy(event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.errorId)))
            }

        } else _state.value = DetallePedidoState(pedido = pedido, event = null)
    }

    fun errorMostrado() {
        _state.value = _state.value?.copy(event = null)
    }

    fun actPedido(pedido: Pedido) {
        val actualizado = actPedidoUseCase(pedido)
        if (actualizado) {
            _state.value = _state.value?.copy(
                pedido = pedido,
                event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.pedidoAct))
            )
        } else {
            _state.value = _state.value?.copy(
                event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.errorpedidoAct))
            )
        }
    }

    fun btnBorrarClicked(pedido: Pedido?) {
        _state.value?.let {
            if (!borrarPedidoUseCase(it.pedido)) {
                _state.value = _state
                    .value?.copy(event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.errorId)))
            } else {
                _state.value = _state
                    .value?.copy(event = UiEvent.PopBackStack)
            }
        }
    }

    fun volverListado() {
        _state.value = _state.value?.copy(event = UiEvent.PopBackStack)
    }
    fun limpMensaje() {
        _state.value = _state.value?.copy(mensaje= null)
    }

}
class DetallePedidoViewModelFactory(
    private val stringProvider: StringProvider,
    private val actPedidoUseCase: ActPedidoUseCase = ActPedidoUseCase(),
    private val borrarPedidoUseCase: BorrarPedidoUseCase = BorrarPedidoUseCase(),
    private val getPedidosUseCase: GetPedidosUseCase = GetPedidosUseCase()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetallePedidoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetallePedidoViewModel(
                stringProvider,
                actPedidoUseCase,
                borrarPedidoUseCase,
                getPedidosUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


