package com.example.proyecto1.ui.pantallaListadoPedido

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto1.databinding.ActivityListadopedidoBinding
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.ui.common.UiEvent

class ListadoPedidosActivity: ComponentActivity() {
    private val viewModel: ListadoPedidosViewModel by viewModels()
    private lateinit var adapter: PedidoAdapter
    private lateinit var binding: ActivityListadopedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadopedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewConfig()
        observador()
        eventos()
    }

    private fun recyclerViewConfig() {
        adapter = PedidoAdapter(
            actions = object : PedidoAdapter.PedidosActions {
                override fun onItemClick(pedido: Pedido) {
                    viewModel.clickPedido(pedido)
                }
            }
        )
        binding.recyclerViewPedidos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPedidos.adapter = adapter
    }

    private fun observador() {
        viewModel.state.observe(this) { state ->
            adapter.submitList(state.pedidos)

            state.uiEvent?.let { event ->
                when (event) {
                    is UiEvent.Navigate -> {
                        val intent = when (event.route) {
                            "nuevo pedido" -> android.content.Intent(
                                this@ListadoPedidosActivity,
                                com.example.proyecto1.ui.pantallaAddPedido.AddPedidoActivity::class.java
                            )
                            //dos intents y aplicar el put extra con id
                            "detalle pedido" -> android.content.Intent(
                                this@ListadoPedidosActivity,
                                com.example.proyecto1.ui.pantallaDetallePedido.DetallePedidoActivity::class.java
                            )

                        }
                    intent?.let {startActivity(it) }
                        viewModel.limpiarMensaje()
                    }
                    else -> {}
                }

            }
        }

    }

    private fun eventos() {
       binding.NuevoPedido.setOnClickListener {
           viewModel.nuevoPedido()
       }
    }
}