package com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation.databinding.FragmentListaEntrenamientoBinding
import com.example.navigation.domain.model.Entrenamiento
import dagger.hilt.android.AndroidEntryPoint


import kotlin.getValue
@AndroidEntryPoint
class ListaEntrenamientoFragment : Fragment(), ListaEntrenamientoAdapter.EntrenamientoAdapterActions {

    private var _binding: FragmentListaEntrenamientoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListaEntrenamientoViewModel by viewModels ()

    private lateinit var listaEntrenamientoAdapter: ListaEntrenamientoAdapter


    override fun onClickItem(entrenamiento: Entrenamiento) {
        val action = ListaEntrenamientoFragmentDirections.actionListaEntrenamientoFragmentToSesionEntrenamientoFragment(
         entrenamientoId = entrenamiento.id
        )
        findNavController().navigate(action)

        Log.d("FragmentClick", "Clicked on ${entrenamiento.nombre}")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        listaEntrenamientoAdapter = ListaEntrenamientoAdapter(this)
        binding.rvEntrenamientos.apply {
            adapter = listaEntrenamientoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListaEntrenamientoBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            listaEntrenamientoAdapter.submitList(state.entrenamientos)

            // Aquí puedes manejar también el state.mensaje si lo añades a tu State (ej. mostrar ProgressBar o Error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}