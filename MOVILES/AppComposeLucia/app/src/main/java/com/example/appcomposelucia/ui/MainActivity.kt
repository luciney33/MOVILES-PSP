package com.example.appcomposelucia.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appcomposelucia.ui.theme.ComposeAppTheme
import com.example.appcomposelucia.ui.pantallaPedido.PedidoScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAppTheme {
                    PedidoScreenViewModel()

            }
        }
    }
}


