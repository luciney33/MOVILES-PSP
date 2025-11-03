package com.example.proyecto1.ui.pantallaAddPedido
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto1.R
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.domain.usecases.AddPedidoUseCase
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
    private val _state: MutableLiveData<AddPedidoState> = MutableLiveData()
    val state: LiveData<AddPedidoState> get() = _state
    init {
        _state.value = AddPedidoState(
            pedido = Pedido(0, "", "", "", "", "", ""),
            idPedido = 0,
            mensaje = null
        )
    }


    fun btnGuardarClicked(pedido: Pedido) {

        val nuevoPedidoId = addPedidoUseCase.invoke(pedido)
        if (nuevoPedidoId) {
            _state.value = state.value?.copy(
                mensaje = stringProvider.getString(R.string.pedido_guardado)
            )
            _state.value = state.value?.copy(
                uiEvent = UiEvent.PopBackStack
            )
        } else {
            _state.value = state.value?.copy(
                mensaje = stringProvider.getString(R.string.error_guardar)
            )
        }
    }

    fun limpMensaje() {
        _state.value = state.value?.copy(mensaje= null)
    }
    fun limpiarEvento() {
        _state.value = state.value?.copy(uiEvent = null)
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