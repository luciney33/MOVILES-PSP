package com.example.appdragonballapi.ui.pantallaPlanetas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appdragonballapi.R
import com.example.appdragonballapi.databinding.ItemPlanetaBinding
import com.example.appdragonballapi.domain.model.Planet

class PlanetasAdapter :
    ListAdapter<Planet, PlanetasAdapter.PlanetViewHolder>(PlanetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanetViewHolder {
        val binding = ItemPlanetaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlanetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlanetViewHolder(
        private val binding: ItemPlanetaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(planet: Planet) {
            binding.apply {
                tvPlanetName.text = planet.name

                ivPlanet.load(planet.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }
            }
        }
    }

    private class PlanetDiffCallback : DiffUtil.ItemCallback<Planet>() {
        override fun areItemsTheSame(oldItem: Planet, newItem: Planet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Planet, newItem: Planet): Boolean {
            return oldItem == newItem
        }
    }
}
