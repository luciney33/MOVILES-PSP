package com.example.appdragonballapi.ui.pantallaDetalleCharacter

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
import coil.load
import com.example.appdragonballapi.R
import com.example.appdragonballapi.databinding.FragmentDetalleBinding
import com.example.appdragonballapi.ui.common.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetalleFragment : Fragment() {

    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetalleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        observeEvents()

        binding.fabEdit.setOnClickListener {
            viewModel.uiState.value.character?.let {
                val action = DetalleFragmentDirections.actionDetalleFragmentToEditarFragment(it.id)
                findNavController().navigate(action)
            }
        }
    }


    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { state ->
                state.character?.let {
                    binding.collapsingToolbar.title = it.name
                    binding.ivCharacterDetail.load(it.imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(R.drawable.ic_launcher_foreground)
                    }
                    binding.tvKi.text = it.ki
                    binding.tvRace.text = it.race
                    binding.tvDescription.text = it.description
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewModel.events.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { event ->
                when (event) {
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
