package com.example.proyecto1.ui.pantallaListadoPedido

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ActivityListadopedidoBinding
import com.example.proyecto1.domain.model.Pedido
import com.example.proyecto1.ui.common.UiEvent
import com.example.proyecto1.ui.pantallaAddPedido.AddPedidoActivity
import com.example.proyecto1.ui.pantallaDetallePedido.DetallePedidoActivity

class ListadoPedidosActivity: ComponentActivity() {
    private val viewModel: ListadoPedidosViewModel by viewModels()
    private lateinit var adapter: PedidoAdapter
    private lateinit var binding: ActivityListadopedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadopedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewConfig()
        observador()
        eventos()
    }
    override fun onResume() {
        super.onResume()
        viewModel.cargarPedidos()
    }
    private fun recyclerViewConfig() {
        adapter = PedidoAdapter(ItemClick ={ pedido ->
            navigateToDetail(pedido.id)
        },
            actions = object : PedidoAdapter.PedidosActions {
                override fun onItemClick(pedido: Pedido) {
                    navigateToDetail(pedido.id)
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
                when (event){
                    is UiEvent.Navigate ->{
                        navigateToDetail(event.id)
                    }

                    UiEvent.PopBackStack -> TODO()
                    is UiEvent.ShowSnackbar -> TODO()
                }
            }
        }

    }

    private fun navigateToDetail(id: Int){
        val intent = Intent(this, DetallePedidoActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)

    }
    private fun navigateToAdd(){
        val intent = Intent(this, AddPedidoActivity::class.java)
        startActivity(intent)

    }

    private fun eventos() {
       binding.NuevoPedido.setOnClickListener {
           navigateToAdd()
       }
    }
}