package com.example.navigation.ui.menuProgreso.ejercicioProgreso

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.navigation.R
import com.example.navigation.databinding.ItemEjercicioProgresoBinding
import com.example.navigation.domain.model.Progreso

class EjercicioProgresoAdapter(
    private val onClick: (Progreso) -> Unit
) : ListAdapter<Progreso, EjercicioProgresoAdapter.EjercicioProgresoViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EjercicioProgresoViewHolder {
        val binding = ItemEjercicioProgresoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EjercicioProgresoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EjercicioProgresoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

   inner class EjercicioProgresoViewHolder(private val binding: ItemEjercicioProgresoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Progreso) {
            binding.tvNombreEjercicio.text = item.nombre
            binding.tvGrupoMuscular.text = if (item.grupoMuscular.isNotBlank()) item.grupoMuscular else binding.root.context.getString(R.string.progreso_grupo_desconocido)

            val ultimo = if (item.ultimoPeso != "-") item.ultimoPeso else binding.root.context.getString(R.string.progreso_sin_registros)
            binding.tvUltimoPeso.text = ultimo
            val detalle = if (item.ultimoDetalle != "-") item.ultimoDetalle else binding.root.context.getString(R.string.progreso_sin_registros)
            binding.tvUltimoPeso.contentDescription = binding.root.context.getString(R.string.progreso_ultimo_registro_cd, detalle)

            val record = if (item.record != "-") item.record else binding.root.context.getString(R.string.progreso_sin_rm)
            binding.tvRecord.text = record

            val contexto = binding.root.context
            val color = if (item.tendenciaUp) ContextCompat.getColor(contexto, R.color.green_700) else ContextCompat.getColor(contexto, R.color.gray_600)
            binding.ivTendencia.setColorFilter(color)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    class Diff : DiffUtil.ItemCallback<Progreso>() {
        override fun areItemsTheSame(oldItem: Progreso, newItem: Progreso): Boolean = oldItem.ejercicioId == newItem.ejercicioId
        override fun areContentsTheSame(oldItem: Progreso, newItem: Progreso): Boolean = oldItem == newItem
    }
}