package com.example.kotlintask3.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlintask3.R
import com.example.kotlintask3.api.Item
import com.example.kotlintask3.databinding.RecyclerviewItemBinding
import kotlinx.android.synthetic.main.recyclerview_item.view.*


class ItemAdapter(private val data: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.AdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItemBinding = RecyclerviewItemBinding.inflate(inflater, parent, false)
        return AdapterViewHolder(listItemBinding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val item = data[holder.adapterPosition]
        holder.itemView.apply {
            nameTextView.text = item.itemName
            descriptionTextView.text = item.ItemDescription
            Glide.with(this).load(item.itemIconSrc).into(itemImage)
            setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("itemPosition", holder.adapterPosition)
                it.findNavController().navigate(
                    R.id.action_item_List_to_selected_item,
                    bundle
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class AdapterViewHolder(binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}