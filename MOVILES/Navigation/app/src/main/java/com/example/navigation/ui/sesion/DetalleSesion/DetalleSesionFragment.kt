package com.example.navigation.ui.sesion.DetalleSesion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation.databinding.FragmentDetalleSesionBinding
import com.example.navigation.domain.model.SesionEjercicio
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetalleSesionFragment : Fragment() {

    private var _binding: FragmentDetalleSesionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetalleSesionViewModel by viewModels()
    private lateinit var adapter: DetalleSesionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetalleSesionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadingDetalles()
        observer()

    }

    private fun loadingDetalles() {
        val sesionId = arguments?.getInt("sesionId") ?: 0
        if (sesionId != 0) {
            viewModel.load(sesionId)
        }
    }
    private fun setupRecyclerView() {
        adapter = DetalleSesionAdapter { item -> onEjercicioClicked(item) }
        binding.rvEjercicios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEjercicios.adapter = adapter
    }

    private fun observer(){
        viewModel.state.observe(viewLifecycleOwner) { st ->
            if (st.sesion != null) {
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                binding.tvNombreEntrenamiento.text = st.entrenamientoNombre
                binding.tvFecha.text = dateFormat.format(Date(st.sesion.fechaInicio))
                val minutos = (st.sesion.duracionMs / 60000).toInt()
                binding.tvDuracion.text = getString(com.example.navigation.R.string.duracion_prefix, minutos)
            }
            adapter.submitList(st.ejercicios)
        }
    }
    private fun onEjercicioClicked(item: SesionEjercicio) {
        val bundle = Bundle().apply { putInt("ejercicioId", item.ejercicioId) }
        findNavController().navigate(com.example.navigation.R.id.action_detalleSesionFragment_to_detalleEntrenamientoFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}