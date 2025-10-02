package com.example.proyecto1.ui.pantallamain
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ActivityMainBinding

//enganchar con el viewmodel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventos()
        observador()

    }
    private fun eventos(){
        binding.textNombre.addTextChangedListener{
                viewModel.nombreEscrito(it.toString())
        }

        binding.textApellidos.addTextChangedListener{
            viewModel.apellidoEscrito(it.toString())
        }

        binding.textCorreo.addTextChangedListener{
            viewModel.correoEscrito(it.toString())
        }

        binding.textComentarios.addTextChangedListener{
            viewModel.comentarioEscrito(it.toString())
        }

//        binding.phoneTelefono.addTextChangedListener{
//            viewModel.telfEscrito(it.toInt())
//        }
//
//        binding.dateFechaNac.setOnClickListener {
//            viewModel.fechanacEscrito(it.to)
//        }

        binding.rGroup.setOnCheckedChangeListener {group, checkedId ->
            when(checkedId){
                R.id.Mujer->viewModel.sexoSeleccionado("Mujer")
                R.id.Hombre->viewModel.sexoSeleccionado("Hombre")
                R.id.Otro->viewModel.sexoSeleccionado("Otro")
            }
        }

        binding.Anterior.setOnClickListener {
            viewModel.btnAntClicked()
        }

        binding.Siguiente.setOnClickListener {
            viewModel.btnAntClicked()
        }

        binding.btLimpiar.setOnClickListener {
            viewModel.btnAntClicked()
        }

        binding.btAct.setOnClickListener {
            viewModel.btnAntClicked()
        }

        binding.btBorrar.setOnClickListener {
            viewModel.btnAntClicked()
        }

        binding.btGuardar.setOnClickListener {
            viewModel.btnAntClicked()
        }
    }

    private fun observador(){
        viewModel.state.observe(this){state ->
            binding.textNombre.setText(state.stateNombre)
            binding.textApellidos.setText(state.stateApellido)
            binding.textCorreo.setText(state.stateCorreo)
            binding.textComentarios.setText(state.stateComentario)
            binding.phoneTelefono.setText(state.stateTelf)
//            binding.dateFechaNac.

        }
    }

}

