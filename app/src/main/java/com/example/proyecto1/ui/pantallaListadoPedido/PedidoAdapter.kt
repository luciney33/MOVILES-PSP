package com.example.proyecto1.ui.pantallaListadoPedido

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto1.databinding.ItemPedidoBinding
import com.example.proyecto1.domain.model.Pedido

class PedidoAdapter(
    val actions: PedidosActions,
    val onClickView: (Pedido) -> Unit,
) : ListAdapter<Pedido, PedidoAdapter.PedidoViewHolder>(
    PedidoDiffCallBack()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PedidoViewHolder(binding, onClickView, actions)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface PedidosActions {
        fun onItemClick(pedido: Pedido)
    }

    class PedidoViewHolder(
        private val binding: ItemPedidoBinding,
        val onClickView: (Pedido) -> Unit,
        val actions: PedidosActions,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pedido: Pedido) {
            binding.textNomApe.text = pedido.nomape
            binding.textMarca.text = pedido.marca
            binding.textTalla.text = pedido.talla
            binding.root.setOnClickListener {
                onClickView(pedido)
                actions.onItemClick(pedido)
            }
        }
    }

    class PedidoDiffCallBack : DiffUtil.ItemCallback<Pedido>() {
        override fun areItemsTheSame(
            oldItem: Pedido,
            newItem: Pedido
        ): Boolean {
            return oldItem.nomape == newItem.nomape && oldItem.marca == newItem.marca && oldItem.talla == newItem.talla
        }

        override fun areContentsTheSame(
            oldItem: Pedido,
            newItem: Pedido
        ): Boolean {
            return oldItem == newItem
        }
    }

}
