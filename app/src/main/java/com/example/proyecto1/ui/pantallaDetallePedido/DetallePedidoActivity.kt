package com.example.proyecto1.ui.pantallaDetallePedido

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1.R
import com.example.proyecto1.ui.common.StringProvider
import com.example.proyecto1.databinding.ActivityDetallepedidoBinding
import com.example.proyecto1.domain.usecases.ActPedidoUseCase
import com.example.proyecto1.domain.usecases.BorrarPedidoUseCase
import com.example.proyecto1.domain.usecases.GetPedidosUseCase
import com.example.proyecto1.ui.common.UiEvent
import kotlin.getValue
import kotlin.toString

class DetallePedidoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDetallepedidoBinding
    private val viewModel: DetallePedidoViewModel by viewModels{
        DetallePedidoViewModel.DetallePedidoViewModelFactory(
            StringProvider.instance(this),
            ActPedidoUseCase(),
            BorrarPedidoUseCase(),
            GetPedidosUseCase()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetallepedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        intent.extras?.let {
            val idPedido =it.getInt("id")
            viewModel.getPedidos(idPedido)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventos()
        observar()

    }
    private fun observar(){
        viewModel.uiState.observe(this@DetallePedidoActivity){ state ->
            state?.let {
                state.event?.let { event ->
                    if (event is UiEvent.PopBackStack) {
                        this@DetallePedidoActivity.finish()
                    } else if (event is UiEvent.ShowSnackbar) {
                        Toast.makeText(this@DetallePedidoActivity, event.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    viewModel.errorMostrado()
                }


                if (state.event == null)
                    with(binding) {
                        txtNombre.setText(state.pedido.nomape)
                        textEmail.setText(state.pedido.correo)
                        textTelefono.setText(state.pedido.telf)
                        textMarca.setText(state.pedido.marca)
                        textTalla.setText(state.pedido.talla)
                        textObservaciones.setText(state.pedido.comentario)
                    }
            }
        }
    }

    private fun eventos(){
        with(binding){
            btnBorrar.setOnClickListener {
                viewModel.btnBorrarClicked(viewModel.uiState.value?.pedido)
            }
            btnActualizar.setOnClickListener {
                val pedidoActual = viewModel.uiState.value?.pedido
                pedidoActual?.let { p ->
                    val pedidoActualizado = p.copy(
                        nomape = txtNombre.text.toString(),
                        correo = textEmail.text.toString(),
                        telf = textTelefono.text.toString(),
                        marca = textMarca.text.toString(),
                        talla = textTalla.text.toString(),
                        comentario = textObservaciones.text.toString()
                    )
                    viewModel.actPedido(pedidoActualizado)
                }
            }

            btnVolver.setOnClickListener {
                viewModel.volverListado()
            }
        }
    }
}