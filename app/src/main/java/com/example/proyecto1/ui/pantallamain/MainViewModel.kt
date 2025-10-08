package com.example.proyecto1.ui.pantallamain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.domain.usecases.AddPedidoUseCase

class MainViewModel : ViewModel() {
    private var _state : MutableLiveData<MainState> = MutableLiveData(MainState())
    val state : LiveData<MainState> get() = _state
    fun btnAntClicked() {

    }
    fun btnSigClicked() {

    }
    fun btnLimpClicked(pedido: Pedido) {

    }
    fun btnActClicked(pedido: Pedido) {

    }
    fun btnBorrarClicked(pedido: Pedido) {

    }
    fun btnGuardarClicked(pedido: Pedido) {
        val addPedido = AddPedidoUseCase()
        if (addPedido.invoke(pedido)){
        _state.value = _state.value?.copy(mensaje= "Pedido añadido", pedido = pedido)
        }else _state.value = _state.value?.copy(mensaje= "El pedido no se pudo añadir", pedido = pedido)
    }
}