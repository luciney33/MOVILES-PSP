package com.example.proyecto1.ui.pantallamain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.AddPedidoUseCase
import com.example.proyecto1.domain.usecases.VerPedidoUseCase

class MainViewModel : ViewModel() {
    private var _state: MutableLiveData<MainState> = MutableLiveData(MainState())
    val state: LiveData<MainState> get() = _state
    fun btnAntClicked() {

    }

    fun btnSigClicked() {
        val id = _state.value?.idPedido ?: 0
        val pedido = VerPedidoUseCase().invoke(id)
        _state.value = _state.value?.copy(pedido = pedido, idPedido = id+1,
            isDisable = id+1>0)

    }

    fun btnLimpClicked(pedido: Pedido) {
        _state.value = _state.value?.copy(
            pedido = Pedido(),
            mensaje = "Formulario limpiado"
        )

    }

    fun btnActClicked(pedido: Pedido) {
        val id = _state.value?.idPedido ?: return
        val exito = ActPedidoUseCase().invoke(id,pedido)

        _state.value = if (exito) {
            _state.value?.copy(pedido = pedido, mensaje = "Pedido actualizado correctamente")
        } else {
            _state.value?.copy(mensaje = "Error al actualizar el pedido")
        }
    }

    fun btnBorrarClicked(pedido: Pedido) {

    }

    fun btnGuardarClicked(pedido: Pedido) {

        val addPedido = AddPedidoUseCase()
        if (addPedido.invoke(pedido)) {
            _state.value = _state.value?.copy(mensaje = "Pedido añadido", pedido = pedido)
        } else _state.value = _state.value?.copy(mensaje = "El pedido no se pudo añadir")
    }

    fun limpMensaje() {
        _state.value = _state.value?.copy(mensaje= null)
    }
}