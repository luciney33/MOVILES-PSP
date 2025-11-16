package com.example.navigation.ui.menuEntrenamiento.sesionEntrenamiento

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.navigation.databinding.ItemEjercicioSesionBinding
import com.example.navigation.domain.model.SesionEjercicio

class SesionEntrenamientoAdapter(
    private val actions: Actions
) : ListAdapter<SesionEjercicio, SesionEntrenamientoAdapter.SesionEntrenamientoViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SesionEntrenamientoViewHolder {
        val binding = ItemEjercicioSesionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SesionEntrenamientoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SesionEntrenamientoViewHolder, position: Int) {
        holder.bind(getItem(position), actions)
    }

    class SesionEntrenamientoViewHolder(
        private val binding: ItemEjercicioSesionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SesionEjercicio, actions: Actions) {
            binding.tvNombreEjercicio.text = item.nombreEjercicio
            // Mostrar volumen y también series x repeticiones si existen
            val contexto = binding.root.context
            val volumeText = contexto.getString(com.example.navigation.R.string.volumen_placeholder, item.volumenKg)
            val seriesText = if (item.series > 0) "${item.series} x ${item.repeticiones}" else ""
            binding.tvSeriesRecomendadas.text = if (seriesText.isNotEmpty()) "$volumeText • $seriesText" else volumeText

            binding.rvSeries.visibility = android.view.View.GONE // si no se usan series, ocultar

            // El botón 'Actualizar Serie' disparará la acción con el ejercicio
            binding.btnActualizarSerie.setOnClickListener {
                actions.onActualizarSerie(item)
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<SesionEjercicio>() {
        override fun areItemsTheSame(oldItem: SesionEjercicio, newItem: SesionEjercicio): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SesionEjercicio, newItem: SesionEjercicio): Boolean = oldItem == newItem
    }

    interface Actions {
        fun onActualizarSerie(ejercicio: SesionEjercicio)
    }
}