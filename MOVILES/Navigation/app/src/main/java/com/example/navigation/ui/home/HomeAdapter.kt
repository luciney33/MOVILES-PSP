package com.example.navigation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.navigation.R
import com.example.navigation.databinding.ItemSesionHistorialBinding
import com.example.navigation.domain.model.SesionSummary
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapter(
    private val onClick: (SesionSummary) -> Unit
) : ListAdapter<SesionSummary, HomeAdapter.HomeViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemSesionHistorialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

   inner  class HomeViewHolder(private val binding: ItemSesionHistorialBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        fun bind(item: SesionSummary) {
            binding.tvFechaSesion.text = dateFormat.format(Date(item.fechaInicio))
            binding.tvNombreEntrenamiento.text = item.entrenamientoNombre
            val minutos = (item.duracionMs / 60000).toInt()
            binding.tvDuracion.text = binding.root.context.getString(R.string.session_duration, minutos)
            binding.tvEjerciciosCompletados.text = binding.root.context.getString(R.string.session_ejercicios, item.ejerciciosCompletados)
            binding.tvVolumen.text = binding.root.context.getString(R.string.session_volumen, String.format(Locale.getDefault(), "%.1f", item.volumenTotalKg))

            binding.root.setOnClickListener { onClick(item) }
        }
   }

    class Diff : DiffUtil.ItemCallback<SesionSummary>() {
        override fun areItemsTheSame(oldItem: SesionSummary, newItem: SesionSummary): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SesionSummary, newItem: SesionSummary): Boolean = oldItem == newItem
    }
}

