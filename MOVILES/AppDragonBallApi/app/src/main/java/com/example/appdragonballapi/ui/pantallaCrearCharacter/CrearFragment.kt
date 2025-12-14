package com.example.appdragonballapi.ui.pantallaCrearCharacter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.appdragonballapi.databinding.FragmentCrearBinding
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.appdragonballapi.ui.common.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CrearFragment : Fragment() {

    private var _binding: FragmentCrearBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CrearViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeEvents()
    }

    private fun setupListeners() {
        binding.apply {
            etName.doOnTextChanged { text, _, _, _ ->
                viewModel.handleIntent(CrearIntent.OnCajaChange(text.toString(), etKi.text.toString(), etRace.text.toString(), etDescription.text.toString()))
            }
            etKi.doOnTextChanged { text, _, _, _ ->
                viewModel.handleIntent(CrearIntent.OnCajaChange(etName.text.toString(), text.toString(), etRace.text.toString(), etDescription.text.toString()))
            }
            etRace.doOnTextChanged { text, _, _, _ ->
                viewModel.handleIntent(CrearIntent.OnCajaChange(etName.text.toString(), etKi.text.toString(), text.toString(), etDescription.text.toString()))
            }
            etDescription.doOnTextChanged { text, _, _, _ ->
                viewModel.handleIntent(CrearIntent.OnCajaChange(etName.text.toString(), etKi.text.toString(), etRace.text.toString(), text.toString()))
            }

            btnSave.setOnClickListener {
                val character = DragonBallCharacter(
                    id = 0,
                    name = viewModel.uiState.value.name,
                    ki = viewModel.uiState.value.ki,
                    race = viewModel.uiState.value.race,
                    description = viewModel.uiState.value.description,
                    imageUrl = "",
                    planet = null,
                    transformations = emptyList()
                )
                viewModel.handleIntent(CrearIntent.AddCharacter(character))
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewModel.events.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { event ->
                when (event) {
                    is UiEvent.NavigateBack -> findNavController().navigateUp()
                    is UiEvent.ShowSnackbar -> {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
