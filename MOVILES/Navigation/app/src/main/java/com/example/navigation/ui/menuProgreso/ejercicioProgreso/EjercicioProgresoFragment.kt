package com.example.navigation.ui.menuProgreso.ejercicioProgreso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation.databinding.FragmentEjercicioProgresoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EjercicioProgresoFragment : Fragment() {

    private var _binding: FragmentEjercicioProgresoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EjercicioProgresoViewModel by viewModels()
    private lateinit var adapter: EjercicioProgresoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEjercicioProgresoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       navegar()
       setupRecyclerView()
       observeEjercicios()

        viewModel.load()
    }
private fun setupRecyclerView() {
    binding.rvEjercicios.layoutManager = LinearLayoutManager(requireContext())
    binding.rvEjercicios.adapter = adapter

}
    private fun observeEjercicios() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.items)
            binding.tvVacio.visibility = if (state.items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun navegar(){
        adapter = EjercicioProgresoAdapter { item ->
            val bundle = bundleOf("ejercicioId" to item.ejercicioId)
            findNavController().navigate(com.example.navigation.R.id.action_ejercicioProgresoFragment_to_registroProgresoFragment, bundle)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}