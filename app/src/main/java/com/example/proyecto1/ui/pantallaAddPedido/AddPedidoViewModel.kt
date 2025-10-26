package com.example.proyecto1.ui.pantallaAddPedido

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.R
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.AddPedidoUseCase
import com.example.proyecto1.domain.usecases.BorrarPedidoUseCase
import com.example.proyecto1.domain.usecases.TotalPedUseCase
import com.example.proyecto1.domain.usecases.VerPedidoUseCase
import com.example.proyecto1.ui.common.StringProvider
import com.example.proyecto1.ui.common.UiEvent

class AddPedidoViewModel (
    private val stringProvider: StringProvider,
    private val addPedidoUseCase: AddPedidoUseCase,
    private val verPedidoUseCase: VerPedidoUseCase,
    private val totalPedUseCase: TotalPedUseCase
): ViewModel() {
    var state: MutableLiveData<AddPedidoState> = MutableLiveData()
        private set
    init {
        state.value = AddPedidoState(
            pedido = Pedido(0, "", "", "", "", "", "L"), // Pedido vacío inicial
            idPedido = 0,
            totalPedidos = 0,
            mensaje = null
        )
    }

    fun btnAntClicked() {
        val id = state.value?.idPedido ?: 0
        val total = totalPedUseCase.invoke()
        if (id - 1 >= 0) {
            val pedido = verPedidoUseCase.invoke(id - 1)
            state.value = state.value?.copy(
                pedido = pedido,
                idPedido = id - 1,
                totalPedidos = total,
            )
        }
    }


    fun btnSigClicked() {
        val id = state.value?.idPedido ?: 0
        val total = totalPedUseCase.invoke()
        if (id + 1 < total) {
            val pedido = verPedidoUseCase.invoke(id + 1)
            state.value = state.value?.copy(
                pedido = pedido,
                idPedido = id + 1,
                totalPedidos = total,
            )
        }

    }


    fun btnGuardarClicked(pedido: Pedido) {

        val nuevoPedidoId = addPedidoUseCase.invoke(pedido)
        if (nuevoPedidoId) {
            state.value = state.value?.copy(
                mensaje = stringProvider.getString(R.string.pedido_guardado)
            )
            state.value = state.value?.copy(
                uiEvent = UiEvent.PopBackStack
            )
        } else {
            state.value = state.value?.copy(
                mensaje = stringProvider.getString(R.string.error_guardar)
            )
        }
    }

    fun limpMensaje() {
        state.value = state.value?.copy(mensaje= null)
    }
    fun limpiarEvento() {
        state.value = state.value?.copy(uiEvent = null)
    }
}


class AddPedidoViewModelFactory(
    private val stringProvider: StringProvider,
    private val addPedidoUseCase: AddPedidoUseCase = AddPedidoUseCase(),
    private val verPedidoUseCase: VerPedidoUseCase = VerPedidoUseCase(),
    private val totalPedUseCase: TotalPedUseCase = TotalPedUseCase()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPedidoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPedidoViewModel(
                stringProvider,
                addPedidoUseCase,
                verPedidoUseCase,
                totalPedUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}