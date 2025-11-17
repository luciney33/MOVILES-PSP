package com.example.navigation.ui.sesion.DetalleSesion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.navigation.databinding.ItemEjercicioDetalleSesionBinding
import com.example.navigation.domain.model.SesionEjercicio
import java.util.*

class DetalleSesionAdapter(
    private val onClick: (SesionEjercicio) -> Unit
) : ListAdapter<SesionEjercicio, DetalleSesionAdapter.DetalleSesionViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleSesionViewHolder {
        val binding = ItemEjercicioDetalleSesionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetalleSesionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleSesionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DetalleSesionViewHolder(private val binding: ItemEjercicioDetalleSesionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SesionEjercicio) {
            binding.tvNombreEjercicio.text = item.nombreEjercicio

            val ctx = binding.root.context
            if (item.series > 0 && item.repeticiones > 0) {
                binding.tvSeries.text = ctx.getString(com.example.navigation.R.string.series_format, item.series, item.repeticiones)
            } else {
                binding.tvSeries.text = ctx.getString(com.example.navigation.R.string.reps_format, item.repeticiones)
            }

            val volStr = String.format(Locale.getDefault(), "%.1f", item.volumenKg)
            binding.tvVolumenTotal.text = ctx.getString(com.example.navigation.R.string.volumen_total, volStr)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    class Diff : DiffUtil.ItemCallback<SesionEjercicio>() {
        override fun areItemsTheSame(oldItem: SesionEjercicio, newItem: SesionEjercicio): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SesionEjercicio, newItem: SesionEjercicio): Boolean = oldItem == newItem
    }
}