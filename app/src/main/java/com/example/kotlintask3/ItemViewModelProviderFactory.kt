package com.example.kotlintask3

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlintask3.model.ItemViewModel
import com.example.kotlintask3.repository.ItemRepository

class ItemViewModelProviderFactory (
   val app:Application,
    private val itemRepository: ItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ItemViewModel(itemRepository, app) as T
    }
}