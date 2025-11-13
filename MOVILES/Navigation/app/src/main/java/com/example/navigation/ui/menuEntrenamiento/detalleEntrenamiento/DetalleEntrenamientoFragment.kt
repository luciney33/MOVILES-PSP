package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.navigation.databinding.FragmentDetalleEntrenamientoBinding


class DetalleEntrenamientoFragment : Fragment() {
    private var _binding: FragmentDetalleEntrenamientoBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetalleEntrenamientoBinding.inflate(inflater, container, false)
        return binding.root
    }
}