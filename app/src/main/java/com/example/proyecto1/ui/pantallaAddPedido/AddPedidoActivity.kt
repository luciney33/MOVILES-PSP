package com.example.proyecto1.ui.pantallaAddPedido
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ActivityAddpedidoBinding
import com.example.proyecto1.domain.model.Pedido

//enganchar con el viewmodel
class AddPedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddpedidoBinding
    private val viewModel: AddPedidoViewModel by viewModels() {
        AddPedidoViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddpedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventos()
        observador()

    }

    private fun eventos() {

        with(binding) {
            Anterior.setOnClickListener {
                viewModel.btnAntClicked()
            }
            Siguiente.setOnClickListener {
                viewModel.btnSigClicked()
            }

            btLimpiar.setOnClickListener {
                val id = viewModel.state.value?.pedido?.id ?: 0
                val pedido = unPedido(id)
                viewModel.btnLimpClicked(pedido)
            }

            btAct.setOnClickListener {
                val id = viewModel.state.value?.pedido?.id ?: 0
                val pedido = unPedido(id)
                viewModel.btnActClicked(pedido)
            }

            btBorrar.setOnClickListener {
                val id = viewModel.state.value?.pedido?.id ?: 0
                val pedido = unPedido(id)
                viewModel.btnBorrarClicked(pedido)
            }

            btGuardar.setOnClickListener {
                val pedido = unPedido(0)
                viewModel.btnGuardarClicked(pedido)
            }
        }
    }

    private fun observador() {
        viewModel.state.observe(this) { state ->
            with(binding) {
                textNombreApellido.setText(state.pedido?.nomape)
                textCorreo.setText(state.pedido?.correo)
                textComentarios.setText(state.pedido?.comentario)
                phoneTelefono.setText(state.pedido?.telf)
                textMarca.setText(state.pedido?.marca)
                if (state.totalPedidos > 0) {
                    PaginaActual.text = (state.idPedido + 1).toString()
                    TotalPaginas.text = "/" + state.totalPedidos
                } else {
                    PaginaActual.text = "0"
                    TotalPaginas.text = "/0"
                }
                when (state.pedido?.talla) {
                    "L" -> rGroup.check(R.id.L)
                    "M" -> rGroup.check(R.id.M)
                    "S" -> rGroup.check(R.id.S)
                }
            }
            state.mensaje?.let { mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                viewModel.limpMensaje()
            }
        }
    }

    private fun selectedTalla(): String =
        when (binding.rGroup.checkedRadioButtonId) {
            R.id.L -> "L"
            R.id.M -> "M"
            R.id.S -> "S"
            else -> "L"
        }

    private fun unPedido(id: Int): Pedido =
        Pedido(
            id,
            binding.textNombreApellido.text.toString(),
            binding.textCorreo.text.toString(),
            binding.textComentarios.text.toString(),
            binding.phoneTelefono.text.toString(),
            binding.textMarca.text.toString(),
            selectedTalla()
        )
}


