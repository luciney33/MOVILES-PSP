package com.example.navigation.ui.menuProgreso.registroProgreso

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.navigation.R
import com.example.navigation.databinding.FragmentRegistroProgresoBinding
import com.google.android.material.snackbar.Snackbar
import com.example.navigation.data.constants.Constantes
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RegistroProgresoFragment : Fragment() {

    private var _binding: FragmentRegistroProgresoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistroProgresoViewModel by viewModels()

    private var fechaMs: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistroProgresoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventos()

       observar()
    }

    private fun eventos(){
        binding.etFecha.setText(dateFormat.format(fechaMs))

        binding.etFecha.setOnClickListener {
            showDatePicker()
        }

        binding.btnGuardar.setOnClickListener {
            onGuardarClicked()
        }
    }

    private fun observar(){
        viewModel.state.observe(viewLifecycleOwner) { st ->
            binding.btnGuardar.isEnabled = !st.isLoading
            if (st.success) {
                Snackbar.make(binding.root, st.mensaje ?: getString(R.string.guardado), Snackbar.LENGTH_SHORT).show()
                viewModel.clearState()
                findNavController().popBackStack()
            } else if (st.error != null) {
                Snackbar.make(binding.root, Constantes.MENSAJE_ERROR_GENERICO + ": ${st.error}", Snackbar.LENGTH_LONG).show()
                viewModel.clearState()
            }
        }
    }
    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fechaMs
        val dpd = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth, 0, 0, 0)
            fechaMs = c.timeInMillis
            binding.etFecha.setText(dateFormat.format(fechaMs))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun onGuardarClicked() {
        val pesoStr = binding.etPeso.text?.toString()?.trim() ?: ""
        val grasaStr = binding.etGrasa.text?.toString()?.trim() ?: ""
        val notas = ""

        if (pesoStr.isEmpty()) {
            binding.tilPeso.error = getString(R.string.introduce_peso)
            return
        } else {
            binding.tilPeso.error = null
        }

        if (grasaStr.isEmpty()) {
            binding.tilGrasa.error = getString(R.string.introduce_grasa)
            return
        } else {
            binding.tilGrasa.error = null
        }

        val peso = pesoStr.toDoubleOrNull()
        val grasa = grasaStr.toDoubleOrNull()
        if (peso == null) {
            binding.tilPeso.error = getString(R.string.peso_invalido)
            return
        }
        if (grasa == null) {
            binding.tilGrasa.error = getString(R.string.grasa_invalida)
            return
        }

        viewModel.saveProgreso(fechaMs, peso, grasa, notas)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}