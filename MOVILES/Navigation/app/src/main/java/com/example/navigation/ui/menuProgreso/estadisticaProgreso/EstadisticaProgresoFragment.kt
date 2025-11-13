package com.example.navigation.ui.menuProgreso.estadisticaProgreso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.navigation.R
import com.example.navigation.databinding.FragmentEstadisticaProgresoBinding
import com.example.navigation.databinding.FragmentRegistroProgresoBinding

class EstadisticaProgresoFragment : Fragment() {

    private var _binding: FragmentEstadisticaProgresoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEstadisticaProgresoBinding.inflate(inflater, container, false)
        return binding.root
    }
}