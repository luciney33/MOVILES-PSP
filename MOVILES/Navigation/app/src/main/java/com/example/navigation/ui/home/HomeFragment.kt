package com.example.navigation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.navigation.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeAdapter { item ->
            val bundle = Bundle().apply { putInt("sesionId", item.id) }
            findNavController().navigate(com.example.navigation.R.id.action_home_fragment_to_detalleSesionFragment, bundle)
        }

        binding.rvHistorialSesiones.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistorialSesiones.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { st ->
            adapter.submitList(st.sesiones)
            binding.tvHistorialVacio.visibility = if (st.sesiones.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}