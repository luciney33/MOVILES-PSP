package com.example.proyecto1.ui.pantallaDetallePedido
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.ui.common.UiEvent

data class DetallePedidoState(
    val pedido: Pedido,
    val mensaje: String? = null,
    val event: UiEvent? = null
)
