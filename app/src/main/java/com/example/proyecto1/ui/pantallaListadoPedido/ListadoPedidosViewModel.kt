package com.example.proyecto1.ui.pantallaListadoPedido
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.domain.usecases.GetPedidosUseCase
import com.example.proyecto1.ui.pantallaAddPedido.AddPedidoViewModel


class ListadoPedidosViewModel (
    private val getPedidosUseCase : GetPedidosUseCase
): ViewModel() {

    var state: MutableLiveData<ListadoPedidosState> = MutableLiveData()
        private set

    init {
        state.value = ListadoPedidosState()
        cargarPedidos()
    }

    fun cargarPedidos() {
        val pedidos = getPedidosUseCase()
        state.value = state.value?.copy(
            pedidos = pedidos,
            totalPedidos = pedidos.size
        )
    }

}

class ListadoPedidosViewModelFactory(
    private val getPedidosUseCase: GetPedidosUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListadoPedidosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListadoPedidosViewModel(
                getPedidosUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}