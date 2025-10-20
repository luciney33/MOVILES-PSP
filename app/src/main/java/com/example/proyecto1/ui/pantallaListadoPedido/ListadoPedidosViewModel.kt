package com.example.proyecto1.ui.pantallaListadoPedido
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.domain.usecases.GetPedidosUseCase


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


}