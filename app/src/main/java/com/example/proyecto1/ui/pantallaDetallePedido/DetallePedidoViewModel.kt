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
import com.example.proyecto1.ui.common.Constantes
import com.example.proyecto1.ui.common.StringProvider
import com.example.proyecto1.ui.common.UiEvent


class DetallePedidoViewModel(
    private val stringProvider: StringProvider,
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
    fun errorMostrado() {
        _uiState.value = _uiState.value?.copy(event = null)
    }

    fun actPedido(pedido: Pedido) {
        val actualizado = actPedidoUseCase(pedido)
        if (actualizado) {
            _uiState.value = _uiState.value?.copy(
                pedido = pedido,
                event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.pedidoAct))
            )
        } else {
            _uiState.value = _uiState.value?.copy(
                event = UiEvent.ShowSnackbar(stringProvider.getString(R.string.errorpedidoAct))
            )
        }
    }
    fun btnBorrarClicked(pedido: Pedido?) {
        _uiState.value?.let {
            if (!borrarPedidoUseCase(it.pedido)) {
                _uiState.value = _uiState
                    .value?.copy(event = UiEvent.ShowSnackbar(Constantes.ERROR))
            } else {
                _uiState.value = _uiState
                    .value?.copy(event = UiEvent.PopBackStack)
            }
        }
    }

    fun volverListado() {
        _uiState.value = _uiState.value?.copy(event = UiEvent.PopBackStack)
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
}


