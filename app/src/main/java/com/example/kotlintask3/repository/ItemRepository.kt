package com.example.kotlintask3.repository

import com.example.kotlintask3.api.Item
import com.example.kotlintask3.database.ItemRoomDatabase

class ItemRepository(
    private val db: ItemRoomDatabase
) {
    suspend fun insertItems(item: Item) = db.itemDao().insert(item)

    suspend fun getData()=db.itemDao().getItems()

    suspend fun updateName(id:String,name:String)=db.itemDao().updateName(id,name)
}