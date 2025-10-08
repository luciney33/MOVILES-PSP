package com.example.proyecto1.ui.pantallamain
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ActivityMainBinding
import com.example.proyecto1.domain.model.Pedido

//enganchar con el viewmodel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventos()
        observador()

    }
    private fun eventos(){
        binding.Anterior.setOnClickListener {
            viewModel.btnAntClicked()
        }

        binding.Siguiente.setOnClickListener {
            viewModel.btnSigClicked()
        }

        binding.btLimpiar.setOnClickListener {
            val tallaSelec = when(binding.rGroup.checkedRadioButtonId){
                R.id.L -> "L"
                R.id.M ->"M"
                R.id.S -> "S"
                else -> {"L"}
            }
           var pedido = Pedido(
               binding.textNombreApellido.text.toString(),
               binding.textCorreo.text.toString(),
            binding.textComentarios.text.toString(),
            binding.phoneTelefono.text.toString(),
            binding.textMarca.text.toString(),
               tallaSelec

           )
            viewModel.btnLimpClicked(pedido)
        }

        binding.btAct.setOnClickListener {
            val tallaSelec = when(binding.rGroup.checkedRadioButtonId){
                R.id.L -> "L"
                R.id.M ->"M"
                R.id.S -> "S"
                else -> {"L"}
            }
            var pedido = Pedido(
                binding.textNombreApellido.text.toString(),
                binding.textCorreo.text.toString(),
                binding.textComentarios.text.toString(),
                binding.phoneTelefono.text.toString(),
                binding.textMarca.text.toString(),
                tallaSelec
                )
            viewModel.btnActClicked(pedido)
        }

        binding.btBorrar.setOnClickListener {
            val tallaSelec = when(binding.rGroup.checkedRadioButtonId){
                R.id.L -> "L"
                R.id.M ->"M"
                R.id.S -> "S"
                else -> {"L"}
            }
            var pedido = Pedido(
                binding.textNombreApellido.text.toString(),
                binding.textCorreo.text.toString(),
                binding.textComentarios.text.toString(),
                binding.phoneTelefono.text.toString(),
                binding.textMarca.text.toString(),
                tallaSelec
                )
            viewModel.btnBorrarClicked(pedido)
        }

        binding.btGuardar.setOnClickListener {
            val tallaSelec = when(binding.rGroup.checkedRadioButtonId){
                R.id.L -> "L"
                R.id.M ->"M"
                R.id.S -> "S"
                else -> {"L"}
            }

            var pedido = Pedido(
                binding.textNombreApellido.text.toString(),
                binding.textCorreo.text.toString(),
                binding.textComentarios.text.toString(),
                binding.phoneTelefono.text.toString(),
                binding.textMarca.text.toString(),
                tallaSelec
                )
            viewModel.btnGuardarClicked(pedido)
        }
    }

    private fun observador(){
        viewModel.state.observe(this){state ->
            binding.textNombreApellido.setText(state.pedido.nomape)
            binding.textCorreo.setText(state.pedido.correo)
            binding.textComentarios.setText(state.pedido.comentario)
            binding.phoneTelefono.setText(state.pedido.telf)
            binding.textMarca.setText(state.pedido.marca)
            when (state.pedido.talla) {
                "L" -> binding.rGroup.check(R.id.L)
                "M" -> binding.rGroup.check(R.id.M)
                "S" -> binding.rGroup.check(R.id.S)
            }
        }
    }

}

