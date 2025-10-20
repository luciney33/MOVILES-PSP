package com.example.proyecto1.ui.pantallaDetallePedido

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ActivityDetallepedidoBinding
import kotlin.getValue

class DetallePedidoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDetallepedidoBinding
    private val viewModel: DetallePedidoViewModel by viewModels(){
        DetallePedidoViewModel.DetallePedidoViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetallepedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detallepedido)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
}