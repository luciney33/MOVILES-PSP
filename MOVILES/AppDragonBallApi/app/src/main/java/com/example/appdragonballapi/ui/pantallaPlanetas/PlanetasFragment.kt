package com.example.appdragonballapi.ui.pantallaPlanetas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appdragonballapi.databinding.FragmentPlanetasBinding
import com.example.appdragonballapi.ui.common.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlanetasFragment : Fragment() {
    private var _binding: FragmentPlanetasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlanetasViewModel by viewModels()
    private lateinit var planetasAdapter: PlanetasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
        observeEvents()
    }

    private fun setupRecyclerView() {
        planetasAdapter = PlanetasAdapter()
        binding.rvPlanets.apply {
            adapter = planetasAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.rvPlanets.isVisible = !state.isLoading
                    binding.progressBar.isVisible = state.isLoading
                    binding.tvEmptyState.isVisible = !state.isLoading && state.planets.isEmpty()
                    planetasAdapter.submitList(state.planets)
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect {
                    when (it) {
                        is UiEvent.ShowSnackbar -> {
                            Snackbar.make(binding.root, it.message, Snackbar.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
