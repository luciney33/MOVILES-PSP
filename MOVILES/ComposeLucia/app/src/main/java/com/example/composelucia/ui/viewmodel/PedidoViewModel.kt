package com.example.composelucia.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composelucia.domain.model.Pedido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()

}

data class UserFormState(
    val pedidos: List<Pedido> = emptyList(),
    val indiceActual: Int = -1,
    val pedidoActual: Pedido = Pedido()
)

@HiltViewModel
class PedidoViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UserFormState())
    val uiState: StateFlow<UserFormState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun cargarPedido(indice: Int) {
        viewModelScope.launch {
            val pedidos = _uiState.value.pedidos
            if (indice >= 0 && indice < pedidos.size) {
                val usuario = pedidos[indice]
                _uiState.update {
                    it.copy(
                        indiceActual = indice,
                        pedidoActual = usuario.copy()
                    )
                }
            }
        }
    }

    fun actualizarPedido(pedido: Pedido) {
        _uiState.update {
            it.copy(pedidoActual = pedido)
        }
    }

    fun limpiarFormulario() {
        _uiState.update {
            it.copy(
                pedidoActual = Pedido(),
                indiceActual = -1
            )
        }
    }

    fun guardarPedido() {
        viewModelScope.launch {
            val state = _uiState.value
            val pedido = state.pedidoActual

            // Validaciones
            if (pedido.nomape.isBlank()) {
                sendEvent(UiEvent.ShowSnackbar("El nombre es obligatorio"))
                return@launch
            }

            if (pedido.correo.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(pedido.correo).matches()) {
                sendEvent(UiEvent.ShowSnackbar("El email no es válido"))
                return@launch
            }

            if (pedido.telf.isNotBlank() && pedido.telf.length < 9) {
                sendEvent(UiEvent.ShowSnackbar("El teléfono debe tener al menos 9 dígitos"))
                return@launch
            }

            val nuevoPedido = pedido.copy()
            val nuevaLista = state.pedidos.toMutableList().apply {
                add(nuevoPedido)
            }

            _uiState.update {
                it.copy(
                    pedidos = nuevaLista,
                    indiceActual = nuevaLista.size - 1
                )
            }

            limpiarFormulario()
            sendEvent(UiEvent.ShowSnackbar("Pedido guardado correctamente"))
        }
    }

    fun guardarCambiosPedido() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.indiceActual >= 0 && state.indiceActual < state.pedidos.size) {
                val pedido = state.pedidoActual

                // Validaciones
                if (pedido.nomape.isBlank()) {
                    sendEvent(UiEvent.ShowSnackbar("El nombre es obligatorio"))
                    return@launch
                }

                if (pedido.correo.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(pedido.correo).matches()) {
                    sendEvent(UiEvent.ShowSnackbar("El email no es válido"))
                    return@launch
                }

                if (pedido.telf.isNotBlank() && pedido.telf.length < 9) {
                    sendEvent(UiEvent.ShowSnackbar("El teléfono debe tener al menos 9 dígitos"))
                    return@launch
                }

                val pedidoActualizado = pedido.copy()
                val nuevaLista = state.pedidos.toMutableList().apply {
                    set(state.indiceActual, pedidoActualizado)
                }

                _uiState.update {
                    it.copy(pedidos = nuevaLista)
                }

                sendEvent(UiEvent.ShowSnackbar("Pedido actualizado correctamente"))
            } else {
                sendEvent(UiEvent.ShowSnackbar("No hay pedido seleccionado para actualizar"))
            }
        }
    }

    fun borrarPedido() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.indiceActual >= 0 && state.indiceActual < state.pedidos.size) {
                val nuevaLista = state.pedidos.toMutableList().apply {
                    removeAt(state.indiceActual)
                }

                if (nuevaLista.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            pedidos = nuevaLista,
                            indiceActual = -1,
                            pedidoActual = Pedido()
                        )
                    }
                } else {
                    val nuevoIndice = if (state.indiceActual >= nuevaLista.size) {
                        nuevaLista.size - 1
                    } else {
                        state.indiceActual
                    }

                    _uiState.update {
                        it.copy(
                            pedidos = nuevaLista,
                            indiceActual = nuevoIndice
                        )
                    }

                    cargarPedido(nuevoIndice)
                }

                sendEvent(UiEvent.ShowSnackbar("Pedido borrado correctamente"))
            } else {
                sendEvent(UiEvent.ShowSnackbar("No hay pedido seleccionado para borrar"))
            }
        }
    }

    fun navegarAnterior() {
        val state = _uiState.value
        if (state.indiceActual > 0) {
            cargarPedido(state.indiceActual - 1)
        }
    }

    fun navegarSiguiente() {
        val state = _uiState.value
        if (state.indiceActual < state.pedidos.size - 1) {
            cargarPedido(state.indiceActual + 1)
        }
    }
}

