package com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.navigation.databinding.ItemEntrenamientoBinding
import com.example.navigation.domain.model.Entrenamiento

class ListaEntrenamientoAdapter(
    val actions : EntrenamientoAdapterActions
) : ListAdapter<Entrenamiento, ListaEntrenamientoAdapter.ListaEntrenamientoHolder>(ListaEntreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaEntrenamientoHolder {
        val binding = ItemEntrenamientoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListaEntrenamientoHolder(binding, actions)
    }

    override fun onBindViewHolder(holder: ListaEntrenamientoHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ListaEntrenamientoHolder(
        private val binding : ItemEntrenamientoBinding,
        private val actions: EntrenamientoAdapterActions
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entrenamiento: Entrenamiento) {
            binding.tvNombreEntrenamiento.text = entrenamiento.nombre
            binding.tvDescripcionEntrenamiento.text = entrenamiento.descripcion
            binding.tvNumeroEjercicios.text = "${entrenamiento.ejercicios.size} ejercicios"

            // Añadido: manejar el click y notificar al Fragment
            binding.root.setOnClickListener {
                actions.onClickItem(entrenamiento)
            }
        }
    }

    class ListaEntreDiffCallback : DiffUtil.ItemCallback<Entrenamiento>() {
        override fun areItemsTheSame(oldItem: Entrenamiento, newItem: Entrenamiento): Boolean{
            // Solo verifica el ID único
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Entrenamiento, newItem: Entrenamiento) : Boolean{
            return oldItem == newItem
        }
    }

    interface EntrenamientoAdapterActions {
        fun onClickItem(entrenamiento: Entrenamiento)
    }
}