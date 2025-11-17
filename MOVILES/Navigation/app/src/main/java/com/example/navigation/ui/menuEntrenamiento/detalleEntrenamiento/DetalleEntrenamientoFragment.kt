package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation.databinding.FragmentDetalleEntrenamientoBinding
import com.example.navigation.domain.model.SesionEjercicio
import com.google.android.material.snackbar.Snackbar
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

      setupRecyclerView()

        observeViewModel()
        eventos()

        viewModel.loadEjercicio(ejercicioId)

    }
    private fun setupRecyclerView(){
    adapter = DetalleEntrenamientoAdapter(this)
    binding.rvEjercicios.adapter = adapter
    binding.rvEjercicios.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun eventos(){
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
                Snackbar.make(binding.root, getString(com.example.navigation.R.string.mensaje_error_carga_ejercicio), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.ejercicio?.let { ej ->
                binding.etNombre.setText(ej.nombreEjercicio)
                binding.etVolumen.setText(ej.volumenKg.toString())
                binding.etDescripcion.setText(ej.notas)

                editedEjercicio = ej
                adapter.submitList(listOf(ej))

                binding.rvEjercicios.visibility = View.VISIBLE
                binding.cardInfo.visibility = View.VISIBLE
            } ?: run {
                adapter.submitList(emptyList())
                binding.rvEjercicios.visibility = View.GONE
            }

            state.mensaje?.let { msg ->
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
            }

            if (state.guardado) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun eliminar(ejercicio: SesionEjercicio) {
        adapter.submitList(emptyList())
        editedEjercicio = null
        binding.etNombre.setText("")
        binding.etVolumen.setText("")
        binding.etDescripcion.setText("")
        Snackbar.make(binding.root, getString(com.example.navigation.R.string.ejercicio_eliminado_vista), Snackbar.LENGTH_SHORT).show()
    }

    override fun campoCambiado(ejercicio: SesionEjercicio) {
        editedEjercicio = ejercicio
        binding.etVolumen.setText(ejercicio.volumenKg.toString())
        binding.etDescripcion.setText(ejercicio.notas)
    }
}