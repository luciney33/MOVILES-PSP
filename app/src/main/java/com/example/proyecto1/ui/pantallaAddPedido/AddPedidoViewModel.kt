package com.example.proyecto1.ui.pantallaAddPedido

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.AddPedidoUseCase
import com.example.proyecto1.domain.usecases.BorrarPedidoUseCase
import com.example.proyecto1.domain.usecases.TotalPedUseCase
import com.example.proyecto1.domain.usecases.VerPedidoUseCase

class AddPedidoViewModel (
    private val addPedidoUseCase: AddPedidoUseCase,
    private val verPedidoUseCase: VerPedidoUseCase,
    private val actPedidoUseCase: ActPedidoUseCase,
    private val borrarPedidoUseCase: BorrarPedidoUseCase,
    private val totalPedUseCase: TotalPedUseCase
): ViewModel() {
    var state: MutableLiveData<AddPedidoState> = MutableLiveData()
        private set
    init {
        val total = totalPedUseCase.invoke()
        val pedidoInicial = if (total > 0) {
            verPedidoUseCase.invoke(0)
        }else Pedido()

        state = MutableLiveData(
            AddPedidoState(
                pedido = pedidoInicial,
                idPedido = 0,
                totalPedidos = total,
                mensaje = null
            )
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

    fun btnLimpClicked(pedido: Pedido) {
        state.value = state.value?.copy(
            pedido = Pedido(),
            mensaje = "Pantalla limpia"
        )

    }

    fun btnActClicked(pedido: Pedido) {
        val id = state.value?.idPedido ?: return
        val act = actPedidoUseCase.invoke(id,pedido)

        state.value = if (act) {
            val total = totalPedUseCase.invoke()
            state.value?.copy(
                pedido = pedido,
                mensaje = "Pedido actualizado",
                totalPedidos = total
            )
        } else {
            state.value?.copy(mensaje = "Error al actualizar el pedido")
        }
    }

    fun btnBorrarClicked(pedido: Pedido) {
        val id = state.value?.idPedido ?: return
        val borrar = borrarPedidoUseCase.invoke(id)

        if (borrar) {
            val total = totalPedUseCase.invoke()
            val nuevoId = if (id > 0) {
                id - 1
            } else 0
            val pedidoNuevo = if (total > 0){
                VerPedidoUseCase().invoke(nuevoId)}
            else Pedido()

            state.value = state.value?.copy(
                pedido = pedidoNuevo,
                idPedido = nuevoId,
                totalPedidos = total,
                mensaje = "Pedido borrado"
            )
        } else {
            state.value = state.value?.copy(mensaje = "Error al borrar el pedido")
        }
    }

    fun btnGuardarClicked(pedido: Pedido) {

        val nuevoPedidoId = addPedidoUseCase.invoke(pedido)
        if (nuevoPedidoId >= 0) {
            val total = totalPedUseCase.invoke()
            state.value = state.value?.copy(
                mensaje = "Pedido añadido",
                pedido = pedido,
                idPedido = total - 1,
                totalPedidos = total
            )
        } else {
            state.value = state.value?.copy(mensaje = "El pedido no se pudo añadir")
        }
    }

    fun limpMensaje() {
        state.value = state.value?.copy(mensaje= null)
    }
}


class AddPedidoViewModelFactory(
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