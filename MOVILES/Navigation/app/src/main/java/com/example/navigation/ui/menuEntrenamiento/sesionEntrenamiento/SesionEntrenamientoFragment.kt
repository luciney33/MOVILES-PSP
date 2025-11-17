package com.example.navigation.ui.menuEntrenamiento.sesionEntrenamiento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation.databinding.FragmentSesionEntrenamientoBinding
import com.example.navigation.domain.model.SesionEjercicio
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SesionEntrenamientoFragment : Fragment(), SesionEntrenamientoAdapter.Actions {
    private var _binding: FragmentSesionEntrenamientoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SesionEntrenamientoViewModel by viewModels()

    private lateinit var adapter: SesionEntrenamientoAdapter
    private var entrenamientoIdArg: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSesionEntrenamientoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.loadSesion(entrenamientoIdArg)

        observeViewModel()
        eventos()
    }
    private fun setupRecyclerView() {
        adapter = SesionEntrenamientoAdapter(this)
        binding.rvEjercicios.adapter = adapter
        binding.rvEjercicios.layoutManager = LinearLayoutManager(requireContext())

        entrenamientoIdArg = requireArguments().getInt("entrenamientoId", 0)

    }
    private fun observeViewModel() {
        viewModel.tiempoFormateado.observe(viewLifecycleOwner) { t ->
            binding.tvTiempo.text = t
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.ejercicios)
            binding.tvNombreEntrenamiento.text = state.sesion?.let { getString(com.example.navigation.R.string.entrenamiento_label) + " #${it.entrenamientoId}" } ?: getString(com.example.navigation.R.string.entrenamiento_label)
        }
    }

    private fun eventos(){
        binding.btnFinalizarSesion.setOnClickListener {
            val sesionId = viewModel.state.value?.sesion?.id
            if (sesionId != null) {
                viewModel.saveDurationAndStop(sesionId)
                Snackbar.make(binding.root, getString(com.example.navigation.R.string.mensaje_guardado), Snackbar.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, getString(com.example.navigation.R.string.mensaje_error_carga_ejercicio), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActualizarSerie(ejercicio: SesionEjercicio) {
        val bundle = Bundle().apply { putInt("ejercicioId", ejercicio.ejercicioId) }
        findNavController().navigate(com.example.navigation.R.id.action_sesionEntrenamientoFragment_to_detalleEntrenamientoFragment2, bundle)
    }

}