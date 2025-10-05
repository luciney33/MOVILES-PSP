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
        binding.Anterior.setOnClickListener {
            viewModel.btnAntClicked()
        }

        binding.Siguiente.setOnClickListener {
            viewModel.btnSigClicked()
        }

        binding.btLimpiar.setOnClickListener {
            binding.textNombre.text
            binding.textApellidos.text
            binding.textCorreo.text
            binding.textComentarios.text
            binding.dateFechaNac.text
            binding.phoneTelefono.text
            viewModel.btnLimpClicked()
        }

        binding.btAct.setOnClickListener {
            binding.textNombre.text
            binding.textApellidos.text
            binding.textCorreo.text
            binding.textComentarios.text
            binding.dateFechaNac.text
            binding.phoneTelefono.text
            viewModel.btnActClicked()
        }

        binding.btBorrar.setOnClickListener {
            binding.textNombre.text
            binding.textApellidos.text
            binding.textCorreo.text
            binding.textComentarios.text
            binding.dateFechaNac.text
            binding.phoneTelefono.text
            viewModel.btnBorrarClicked()
        }

        binding.btGuardar.setOnClickListener {
            binding.textNombre.text
            binding.textApellidos.text
            binding.textCorreo.text
            binding.textComentarios.text
            binding.dateFechaNac.text
            binding.phoneTelefono.text
            viewModel.btnGuardarClicked()
        }
    }

    private fun observador(){
        viewModel.state.observe(this){state ->
            binding.textNombre.setText(state.stateNombre)
            binding.textApellidos.setText(state.stateApellido)
            binding.textCorreo.setText(state.stateCorreo)
            binding.textComentarios.setText(state.stateComentario)
            binding.phoneTelefono.setText(state.stateTelf)

        }
    }

}

