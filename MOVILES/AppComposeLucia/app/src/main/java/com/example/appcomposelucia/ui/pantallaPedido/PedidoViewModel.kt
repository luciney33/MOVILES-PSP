package com.example.appcomposelucia.ui.pantallaPedido

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcomposelucia.common.Constantes
import com.example.appcomposelucia.domain.model.Pedido
import com.example.appcomposelucia.domain.usecases.ActPedidoUseCase
import com.example.appcomposelucia.domain.usecases.AddPedidoUseCase
import com.example.appcomposelucia.domain.usecases.BorrarPedidoUseCase
import com.example.appcomposelucia.domain.usecases.TotalPedUseCase
import com.example.appcomposelucia.domain.usecases.VerPedidoUseCase
import com.example.appcomposelucia.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedidoViewModel @Inject constructor(
    private val totalPedUseCase: TotalPedUseCase,
    private val verPedidoUseCase: VerPedidoUseCase,
    private val addPedidoUseCase: AddPedidoUseCase,
    private val actPedidoUseCase: ActPedidoUseCase,
    private val borrarPedidoUseCase: BorrarPedidoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PedidoState())
    val uiState: StateFlow<PedidoState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            val total = totalPedUseCase()
            val pedidoInicial = if (total > 0) {
                verPedidoUseCase(0)
            } else {
                Pedido()
            }

            _uiState.update {
                it.copy(
                    pedidoActual = pedidoInicial,
                    indiceActual = if (total > 0) 0 else -1,
                    totalPedidos = total
                )
            }
        }
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun cargarPedido(indice: Int) {
        viewModelScope.launch {
            val total = totalPedUseCase()
            if (indice in 0..<total) {
                val pedido = verPedidoUseCase(indice)
                _uiState.update {
                    it.copy(
                        indiceActual = indice,
                        pedidoActual = pedido,
                        totalPedidos = total
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
                pedidoActual = Pedido()
            )
        }
    }

    fun guardarPedido() {
        viewModelScope.launch {
            val pedido = _uiState.value.pedidoActual

            if (pedido.nomape.isBlank()) {
                sendEvent(UiEvent.ShowSnackbar(Constantes.EL_NOMBRE_ES_OBLIGATORIO))
                return@launch
            }

            if (pedido.correo.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(pedido.correo).matches()) {
                sendEvent(UiEvent.ShowSnackbar(Constantes.EL_EMAIL_NO_ES_VÁLIDO))
                return@launch
            }

            if (pedido.telf.isNotBlank() && pedido.telf.length < 9) {
                sendEvent(UiEvent.ShowSnackbar(Constantes.EL_TELÉFONO_DEBE_TENER_AL_MENOS_9_DÍGITOS))
                return@launch
            }

            val resultado = addPedidoUseCase(pedido)
            if (resultado) {
                val total = totalPedUseCase()
                _uiState.update {
                    it.copy(
                        pedidoActual = pedido,
                        indiceActual = total - 1,
                        totalPedidos = total,
                        mensaje = Constantes.PEDIDO_GUARDADO_CORRECTAMENTE
                    )
                }
                sendEvent(UiEvent.ShowSnackbar(Constantes.PEDIDO_GUARDADO_CORRECTAMENTE))
            } else {
                sendEvent(UiEvent.ShowSnackbar("El pedido no se pudo añadir"))
            }
        }
    }

    fun guardarCambiosPedido() {
        viewModelScope.launch {
            val state = _uiState.value
            val pedido = state.pedidoActual

            if (pedido.nomape.isBlank()) {
                sendEvent(UiEvent.ShowSnackbar(Constantes.EL_NOMBRE_ES_OBLIGATORIO))
                return@launch
            }

            if (pedido.correo.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(pedido.correo).matches()) {
                sendEvent(UiEvent.ShowSnackbar(Constantes.EL_EMAIL_NO_ES_VÁLIDO))
                return@launch
            }

            if (pedido.telf.isNotBlank() && pedido.telf.length < 9) {
                sendEvent(UiEvent.ShowSnackbar(Constantes.EL_TELÉFONO_DEBE_TENER_AL_MENOS_9_DÍGITOS))
                return@launch
            }

            val id = state.indiceActual
            if (id >= 0) {
                val actualizado = actPedidoUseCase(id, pedido)
                if (actualizado) {
                    val total = totalPedUseCase()
                    _uiState.update {
                        it.copy(
                            pedidoActual = pedido,
                            totalPedidos = total,
                            mensaje = Constantes.PEDIDO_ACTUALIZADO_CORRECTAMENTE
                        )
                    }
                    sendEvent(UiEvent.ShowSnackbar(Constantes.PEDIDO_ACTUALIZADO_CORRECTAMENTE))
                } else {
                    sendEvent(UiEvent.ShowSnackbar(Constantes.ERROR_ACT))
                }
            } else {
                sendEvent(UiEvent.ShowSnackbar(Constantes.NO_HAY_PEDIDO))
            }
        }
    }

    fun borrarPedido() {
        viewModelScope.launch {
            val state = _uiState.value
            val id = state.indiceActual

            if (id >= 0) {
                val borrado = borrarPedidoUseCase(id)

                if (borrado) {
                    val total = totalPedUseCase()
                    val nuevoIndice = if (id > 0) {
                        id - 1
                    } else {
                        0
                    }

                    val pedidoNuevo = if (total > 0) {
                        verPedidoUseCase(nuevoIndice)
                    } else {
                        Pedido()
                    }

                    _uiState.update {
                        it.copy(
                            pedidoActual = pedidoNuevo,
                            indiceActual = if (total > 0) nuevoIndice else -1,
                            totalPedidos = total,
                            mensaje = Constantes.PEDIDO_BORRADO
                        )
                    }
                    sendEvent(UiEvent.ShowSnackbar(Constantes.PEDIDO_BORRADO))
                } else {
                    sendEvent(UiEvent.ShowSnackbar(Constantes.ERROR_BORRAR))
                }
            } else {
                sendEvent(UiEvent.ShowSnackbar(Constantes.NO_HAY_PEDIDO2))
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
        if (state.indiceActual < state.totalPedidos - 1) {
            cargarPedido(state.indiceActual + 1)
        }
    }


}
