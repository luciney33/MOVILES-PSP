package com.example.navigation.ui.menuProgreso.registroProgreso

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigation.databinding.FragmentRegistroProgresoBinding

class RegistroProgresoFragment : Fragment() {

    private var _binding: FragmentRegistroProgresoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistroProgresoBinding.inflate(inflater, container, false)
        return binding.root
    }
}