package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

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
import com.example.navigation.databinding.FragmentDetalleEntrenamientoBinding
import com.example.navigation.domain.model.SesionEjercicio
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetalleEntrenamientoFragment : Fragment(), DetalleEntrenamientoAdapter.DetalleEntrenamientoAdapterActions {
    private var _binding: FragmentDetalleEntrenamientoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetalleEntrenamientoViewModel by viewModels()

    private var ejercicioId: Int = 0

    private lateinit var adapter: DetalleEntrenamientoAdapter
    private var editedEjercicio: SesionEjercicio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ejercicioId = requireArguments().getInt("ejercicioId", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetalleEntrenamientoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DetalleEntrenamientoAdapter(this)
        binding.rvEjercicios.adapter = adapter
        binding.rvEjercicios.layoutManager = LinearLayoutManager(requireContext())

        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.ejercicio?.let { ej ->
                Log.d("DetalleFragment", "state.ejercicio.id=${ej.id} - actualizando lista")
                binding.etNombre.setText(ej.nombreEjercicio)
                binding.etVolumen.setText(ej.volumenKg.toString())
                binding.etDescripcion.setText(ej.notas)

                editedEjercicio = ej
                adapter.submitList(listOf(ej))
                adapter.notifyDataSetChanged()

                binding.rvEjercicios.visibility = View.VISIBLE
                binding.cardInfo.visibility = View.VISIBLE
            } ?: run {
                Log.d("DetalleFragment", "state.ejercicio es null - limpiar lista")
                adapter.submitList(emptyList())
                adapter.notifyDataSetChanged()
                binding.rvEjercicios.visibility = View.GONE
            }

            state.mensaje?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }

            if (state.guardado) {
                findNavController().navigateUp()
            }
        }

        // Cargar ejercicio
        Log.d("DetalleFragment", "cargando ejercicioIdArg=$ejercicioId")
        viewModel.loadEjercicio(ejercicioId)

        binding.btnGuardar.setOnClickListener {
            val detalle = editedEjercicio ?: viewModel.state.value?.ejercicio
            if (detalle != null) {
                val volumen = binding.etVolumen.text.toString().toDoubleOrNull() ?: detalle.volumenKg
                val notas = binding.etDescripcion.text.toString()
                val updated = detalle.copy(volumenKg = volumen, notas = notas)

                val rel = editedEjercicio ?: detalle
                val series = rel.series
                val reps = rel.repeticiones
                val sesionId = rel.sesionId
                val ejercicioId = rel.ejercicioId

                viewModel.actualizar(sesionId, ejercicioId, series, reps)

                viewModel.actualizarEjercicio(updated)

            } else {
                Toast.makeText(requireContext(), "No hay ejercicio cargado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun eliminar(ejercicio: SesionEjercicio) {
        adapter.submitList(emptyList())
        adapter.notifyDataSetChanged()
        editedEjercicio = null
        binding.etNombre.setText("")
        binding.etVolumen.setText("")
        binding.etDescripcion.setText("")
        Toast.makeText(requireContext(), "Ejercicio eliminado de la vista", Toast.LENGTH_SHORT).show()
    }

    override fun campoCambiado(ejercicio: SesionEjercicio) {
        editedEjercicio = ejercicio
        binding.etVolumen.setText(ejercicio.volumenKg.toString())
        binding.etDescripcion.setText(ejercicio.notas)
    }
}