package com.example.appdragonballapi.ui.pantallaListaCharacters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appdragonballapi.databinding.FragmentListaBinding
import com.example.appdragonballapi.domain.model.DragonBallCharacter
import com.example.appdragonballapi.ui.common.UiEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListaFragment : Fragment() {
    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListaViewModel by viewModels()
    private lateinit var adapter: ListaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeCharacters()
        observeEvents()
    }

    private fun setupClickListeners() {
        binding.fabAddCharacter.setOnClickListener {
            val action = ListaFragmentDirections.actionListaFragmentToCrearFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        adapter = ListaAdapter(
            actions = object : ListaAdapter.CharacterActions {
                override fun onCharacterClick(characterId: Int) {
                    val action = ListaFragmentDirections.actionListaFragmentToDetalleFragment(characterId)
                    findNavController().navigate(action)
                }

                override fun onCharacterEdit(character: DragonBallCharacter) {
                    val action = ListaFragmentDirections.actionListaFragmentToEditarFragment(character.id)
                    findNavController().navigate(action)
                }

                override fun onCharacterDelete(character: DragonBallCharacter) {
                    viewModel.handleIntent(DragonBallIntent.DeleteCharacter(character.id))
                }

                override fun onCharacterTransformations(characterId: Int) {
                    val action = ListaFragmentDirections.actionListaFragmentToTransformacionesFragment(characterId)
                    findNavController().navigate(action)
                }
            }
        )

        binding.recyclerViewCharacters.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ListaFragment.adapter
            setHasFixedSize(true)
        }
    }


    private fun observeCharacters() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.characters)
                    binding.tvEmptyState.isVisible = state.characters.isEmpty() && !state.isLoading
                    binding.progressBar.isVisible = state.isLoading
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> {
                            Snackbar.make(
                                binding.root,
                                event.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is UiEvent.ShowError -> {
                            Snackbar.make(
                                binding.root,
                                event.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        is UiEvent.NavigateBack -> {
                            findNavController().navigateUp()
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
