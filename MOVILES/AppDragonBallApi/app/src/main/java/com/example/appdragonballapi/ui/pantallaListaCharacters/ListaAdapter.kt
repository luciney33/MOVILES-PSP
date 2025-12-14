package com.example.appdragonballapi.ui.pantallaListaCharacters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.appdragonballapi.R
import com.example.appdragonballapi.databinding.ItemDragonballCharacterBinding
import com.example.appdragonballapi.domain.model.DragonBallCharacter

class ListaAdapter(
    private val actions: CharacterActions,
) : ListAdapter<DragonBallCharacter, ListaAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    interface CharacterActions {
        fun onCharacterClick(characterId: Int)
        fun onCharacterEdit(character: DragonBallCharacter)
        fun onCharacterDelete(character: DragonBallCharacter)
        fun onCharacterTransformations(characterId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemDragonballCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding, actions)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CharacterViewHolder(
        private val binding: ItemDragonballCharacterBinding,
        private val actions: CharacterActions
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: DragonBallCharacter) {
            binding.apply {
                tvCharacterName.text = character.name
                tvCharacterKi.text = character.ki
                tvCharacterRace.text = character.race
                tvCharacterDescription.text = character.description

                ivCharacter.load(character.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }

                root.setOnClickListener {
                    actions.onCharacterClick(character.id)
                }

                btnTransformations.setOnClickListener { 
                    actions.onCharacterTransformations(character.id)
                }

                ivOverflow.setOnClickListener { view ->
                    val menu = PopupMenu(view.context, view)
                    menu.inflate(R.menu.character_item_menu)
                    menu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.menu_edit -> {
                                actions.onCharacterEdit(character)
                                true
                            }
                            R.id.menu_delete -> {
                                actions.onCharacterDelete(character)
                                true
                            }
                            else -> false
                        }
                    }
                    menu.show()
                }
            }
        }
    }

    private class CharacterDiffCallback : DiffUtil.ItemCallback<DragonBallCharacter>() {
        override fun areItemsTheSame(
            oldItem: DragonBallCharacter,
            newItem: DragonBallCharacter
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: DragonBallCharacter,
            newItem: DragonBallCharacter
        ): Boolean = oldItem == newItem
    }
}