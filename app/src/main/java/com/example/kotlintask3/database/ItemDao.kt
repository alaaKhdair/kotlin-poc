package com.example.kotlintask3.database

import androidx.annotation.NonNull
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kotlintask3.api.Item

@Dao
interface ItemDao {
    @Query("SELECT * from ApiData ORDER BY name ASC")
    suspend fun getItems(): List<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(@NonNull items: Item): Long

    @Query("UPDATE ApiData SET name =:name WHERE id=:id")
    suspend fun updateName(id: String, name: String): Int
}