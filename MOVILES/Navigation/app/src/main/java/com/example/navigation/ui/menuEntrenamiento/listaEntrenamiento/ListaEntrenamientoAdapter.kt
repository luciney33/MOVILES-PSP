package com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.navigation.databinding.ItemEntrenamientoBinding
import com.example.navigation.domain.model.Entrenamiento
import com.example.navigation.domain.model.EntrenamientoConEjercicios

class ListaEntrenamientoAdapter : RecyclerView.Adapter<ListaEntrenamientoAdapter.ListaEntrenamientoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaEntrenamientoHolder {
        val binding = ItemEntrenamientoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListaEntrenamientoHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ListaEntrenamientoHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ListaEntrenamientoHolder(private val binding: ItemEntrenamientoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entrenamiento: Entrenamiento, entrenamientoConEjercicios: EntrenamientoConEjercicios) {
            binding.tvNombreEntrenamiento.text = "${entrenamiento.nombre}"
            binding.tvDescripcionEntrenamiento.text = "${entrenamiento.descripcion}"
            binding.tvNumeroEjercicios.text = "${entrenamientoConEjercicios.ejercicios.size}"
            binding.tvUltimaVez.text
        }
    }
}