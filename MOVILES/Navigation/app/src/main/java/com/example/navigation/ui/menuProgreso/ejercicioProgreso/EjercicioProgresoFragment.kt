package com.example.navigation.ui.menuProgreso.ejercicioProgreso

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigation.R
import com.example.navigation.databinding.FragmentEjercicioProgresoBinding
import com.example.navigation.databinding.FragmentEstadisticaProgresoBinding

class EjercicioProgresoFragment : Fragment() {


    private var _binding: FragmentEjercicioProgresoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEjercicioProgresoBinding.inflate(inflater, container, false)
        return binding.root
    }


}