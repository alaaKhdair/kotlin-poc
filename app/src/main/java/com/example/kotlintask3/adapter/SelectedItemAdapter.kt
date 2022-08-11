package com.example.kotlintask3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlintask3.api.Item
import com.example.kotlintask3.databinding.RecyclerSelectedItemBinding
import com.example.kotlintask3.ui.UiFunction
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import kotlinx.android.synthetic.main.recycler_selected_item.view.*

class SelectedItemAdapter(
    private val data: List<Item>,
    private val uiFunction: UiFunction
) : RecyclerView.Adapter<SelectedItemAdapter.SelectedItemViewHolder>() {

    inner class SelectedItemViewHolder(binding: RecyclerSelectedItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItemBinding = RecyclerSelectedItemBinding.inflate(inflater, parent, false)
        return SelectedItemViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(holder: SelectedItemViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        holder.itemView.apply {
            Glide.with(selectedItemImage).load(item.itemImage)
                .into(selectedItemImage)
            selectedItemTitle.setText(item.itemName, TextView.BufferType.EDITABLE)
            selectedItemDescription.text = item.ItemDescription
            updateNameButton.setOnClickListener {
                val oldName: String = item.itemName
                val newName: String = selectedItemTitle.text.toString()
                uiFunction.updateName(item.id, item, oldName, newName)
            }
            downloadButton.setOnClickListener {
                uiFunction.downloadImage(item.itemImage, item.itemName)
            }
            progressBar?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}