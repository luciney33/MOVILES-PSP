package com.example.proyecto1.ui.pantallamain
import android.widget.Button
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto1.R
import com.example.proyecto1.databinding.ActivityMainBinding

//enganchar con el viewmodel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(

        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.button).setOnClickListener{
            Toast.makeText(this,"holiiii",Toast.LENGTH_LONG).show()
        }
    }
}

