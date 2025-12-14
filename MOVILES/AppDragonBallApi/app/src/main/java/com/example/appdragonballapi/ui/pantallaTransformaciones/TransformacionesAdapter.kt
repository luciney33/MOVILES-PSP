package com.example.appdragonballapi.ui.pantallaTransformaciones

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appdragonballapi.R
import com.example.appdragonballapi.databinding.ItemTransformationBinding
import com.example.appdragonballapi.domain.model.Transformation

class TransformacionesAdapter(
) : ListAdapter<Transformation, TransformacionesAdapter.TransformationViewHolder>(TransformationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransformationViewHolder {
        val binding = ItemTransformationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransformationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransformationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransformationViewHolder(
        private val binding: ItemTransformationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transformation: Transformation) {
            binding.apply {
                tvTransformationName.text = transformation.name
                tvTransformationKi.text = transformation.ki

                ivTransformation.load(transformation.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }

            }
        }
    }

    private class TransformationDiffCallback : DiffUtil.ItemCallback<Transformation>() {
        override fun areItemsTheSame(oldItem: Transformation, newItem: Transformation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transformation, newItem: Transformation): Boolean {
            return oldItem == newItem
        }
    }
}
