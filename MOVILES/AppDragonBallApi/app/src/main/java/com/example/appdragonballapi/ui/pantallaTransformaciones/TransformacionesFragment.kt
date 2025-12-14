package com.example.appdragonballapi.ui.pantallaTransformaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appdragonballapi.databinding.FragmentTransformacionesBinding
import com.example.appdragonballapi.ui.common.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransformacionesFragment : Fragment() {

    private var _binding: FragmentTransformacionesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransformacionesViewModel by viewModels()
    private lateinit var transformacionesAdapter: TransformacionesAdapter
    private val args: TransformacionesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransformacionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.handleIntent(TransformacionesIntent.GetTransformations(args.characterId))
        }
        setupRecyclerView()
        observeState()
        observeEvents()
    }

    private fun setupRecyclerView() {
        transformacionesAdapter = TransformacionesAdapter()
        binding.rvTransformations.apply {
            adapter = transformacionesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    binding.rvTransformations.isVisible = !state.isLoading
                    binding.tvEmptyState.isVisible = !state.isLoading && state.transformations.isEmpty()
                    transformacionesAdapter.submitList(state.transformations)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
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