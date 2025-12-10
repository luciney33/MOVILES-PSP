package com.example.appdragonballapi.ui.pantallaListaCharacters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.appdragonballapi.R
import com.example.appdragonballapi.databinding.FragmentListaBinding
import kotlin.getValue


class ListaFragment : Fragment() {
    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RickMortyViewModel by viewModels()
    private lateinit var adapter: RickMortyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista, container, false)
    }


}