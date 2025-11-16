package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.navigation.databinding.ItemEjercicioEditableBinding
import com.example.navigation.domain.model.SesionEjercicio

class DetalleEntrenamientoAdapter(
    private val actions: DetalleEntrenamientoAdapterActions
) : ListAdapter<SesionEjercicio, DetalleEntrenamientoAdapter.DetalleEntrenamientoViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleEntrenamientoViewHolder {
        val binding = ItemEjercicioEditableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetalleEntrenamientoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleEntrenamientoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, actions)
    }

    class DetalleEntrenamientoViewHolder(
        private val binding: ItemEjercicioEditableBinding) : RecyclerView.ViewHolder(binding.root) {
        private var sesion: SesionEjercicio? = null

        fun bind(item: SesionEjercicio, actions: DetalleEntrenamientoAdapterActions) {
            sesion = item
            Log.d("DetalleAdapter", "bind() item.id=${item.id} nombre=${item.nombreEjercicio}")
            binding.tvNombreEjercicio.text = item.nombreEjercicio

            // Desactivar listeners previos (si hubiera) y asignar valores
            binding.etSeries.onFocusChangeListener = null
            binding.etRepeticiones.onFocusChangeListener = null

            binding.etSeries.setText(item.series.toString())
            binding.etRepeticiones.setText(item.repeticiones.toString())

            binding.btnEliminar.setOnClickListener {
                actions.eliminar(item)
            }

            // Cuando el usuario sale del campo (pierde foco) leemos el valor y notificamos
            binding.etSeries.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val text = binding.etSeries.text?.toString()
                    val series = text?.toIntOrNull() ?: item.series
                    actions.campoCambiado(item.copy(series = series))
                }
            }

            binding.etRepeticiones.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val text = binding.etRepeticiones.text?.toString()
                    val reps = text?.toIntOrNull() ?: item.repeticiones
                    actions.campoCambiado(item.copy(repeticiones = reps))
                }
            }

        }
    }

    class Diff : DiffUtil.ItemCallback<SesionEjercicio>() {
        override fun areItemsTheSame(oldItem: SesionEjercicio, newItem: SesionEjercicio): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SesionEjercicio, newItem: SesionEjercicio): Boolean = oldItem == newItem
    }

    interface DetalleEntrenamientoAdapterActions {
        fun eliminar(ejercicio: SesionEjercicio)
        fun campoCambiado(ejercicio: SesionEjercicio)
    }
}