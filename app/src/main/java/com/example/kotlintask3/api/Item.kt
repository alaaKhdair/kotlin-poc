package com.example.kotlintask3.api

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ApiData")
data class Item (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    var itemName: String,
    @SerializedName("image")
    @ColumnInfo(name = "image")
    val itemImage: String,
    @SerializedName("description")
    @ColumnInfo(name = "description")
    val ItemDescription: String,
    @SerializedName("createdAt")
    @ColumnInfo(name = "createdAt")
    val ItemCreatedAt: String,
    @SerializedName("icon")
    @ColumnInfo(name = "icon")
    val itemIconSrc: String
)