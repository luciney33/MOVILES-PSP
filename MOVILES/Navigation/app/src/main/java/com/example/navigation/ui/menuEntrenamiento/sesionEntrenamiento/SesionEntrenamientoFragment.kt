package com.example.navigation.ui.menuEntrenamiento.sesionEntrenamiento

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation.databinding.FragmentSesionEntrenamientoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SesionEntrenamientoFragment : Fragment(), SesionEntrenamientoAdapter.Actions {
    private var _binding: FragmentSesionEntrenamientoBinding? = null
    private val binding get() = _binding!!

    // usar viewModels para obtener el ViewModel con Hilt
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

        adapter = SesionEntrenamientoAdapter(this)
        binding.rvEjercicios.adapter = adapter
        binding.rvEjercicios.layoutManager = LinearLayoutManager(requireContext())

        // Obtener argumento manualmente para evitar dependencia de SafeArgs generado
        entrenamientoIdArg = requireArguments().getInt("entrenamientoId", 0)

        // Observa el tiempo del cronómetro
        viewModel.tiempoFormateado.observe(viewLifecycleOwner) { t ->
            binding.tvTiempo.text = t
        }

        // Cargar sesión
        viewModel.loadSesion(entrenamientoIdArg)

        // Observa el state (sesion + ejercicios)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.ejercicios)
            binding.tvNombreEntrenamiento.text = state.sesion?.let { "Entrenamiento #${it.entrenamientoId}" } ?: "Entrenamiento"
            // Manejar mensajes de error si existen
            state.mensaje?.let { /* mostrar Snackbar o Toast si quieres */ }
        }

        binding.btnFinalizarSesion.setOnClickListener {
            val sesionId = viewModel.state.value?.sesion?.id
            if (sesionId != null) {
                // Guardar duración y parar cronómetro
                viewModel.saveDurationAndStop(sesionId)
                Toast.makeText(requireContext(), "Sesión finalizada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "No se encontró la sesión a guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // refrescar la sesión al volver del detalle
        if (entrenamientoIdArg != 0) viewModel.loadSesion(entrenamientoIdArg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActualizarSerie(ejercicio: com.example.navigation.domain.model.SesionEjercicio) {
        // Log and toast the id for debugging
        Log.d("SesionFragment", "Navegando a detalle con ejercicioId=${ejercicio.id}")
        Toast.makeText(requireContext(), "Abrir detalle id=${ejercicio.id}", Toast.LENGTH_SHORT).show()

        // Navegar al detalle pasando el id del ejercicio en un Bundle
        val bundle = Bundle().apply { putInt("ejercicioId", ejercicio.id) }
        findNavController().navigate(com.example.navigation.R.id.detalleEntrenamientoFragment, bundle)
    }
}