package com.example.proyecto1.ui.pantallaListadoPedido
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.data.Repositorio
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.domain.usecases.GetPedidosUseCase
import com.example.proyecto1.ui.common.UiEvent


class ListadoPedidosViewModel (private val getPedidosUseCase : GetPedidosUseCase = GetPedidosUseCase()
): ViewModel() {

    var state: MutableLiveData<ListadoPedidosState> = MutableLiveData()
        private set

    init {
        cargarPedidos()
    }

    fun cargarPedidos() {
        val pedidos = getPedidosUseCase()
        state.value = ListadoPedidosState(pedidos = pedidos)
    }

    fun limpiarMensaje() {
        state.value = state.value?.copy(mensaje = null)
    }

    fun clickPedido(pedido: Pedido) {
        val pedidoId = Repositorio.getPedidos().indexOf(pedido)
        state.value = state.value?.copy(
        pedidoId = pedidoId,
        uiEvent = UiEvent.Navigate("detalle pedido"))
    }

    fun nuevoPedido() {
        state.value = state.value?.copy(
        uiEvent = UiEvent.Navigate("nuevo pedido"))
    }

}