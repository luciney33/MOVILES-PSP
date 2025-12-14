package com.example.appdragonballapi.ui.pantallaEditarCharacter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.appdragonballapi.databinding.FragmentEditarBinding
import com.example.appdragonballapi.ui.common.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditarFragment : Fragment() {

    private var _binding: FragmentEditarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditarViewModel by viewModels()
    private val args: EditarFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handleIntent(EditarIntent.LoadCharacter(args.characterId))
        setupListeners()
        observeState()
        observeEvents()
    }

    private fun setupListeners() {
        binding.apply {
            btnSave.setOnClickListener {
                val character = viewModel.uiState.value.character?.copy(
                    name = etName.text.toString(),
                    ki = etKi.text.toString(),
                    race = etRace.text.toString(),
                    description = etDescription.text.toString()
                )
                if (character != null) {
                    viewModel.handleIntent(EditarIntent.UpdateCharacter(character))
                }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { state ->
                state.character?.let {
                    binding.etName.setText(it.name)
                    binding.etKi.setText(it.ki)
                    binding.etRace.setText(it.race)
                    binding.etDescription.setText(it.description)
                }
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
