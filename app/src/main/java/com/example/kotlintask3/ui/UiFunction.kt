package com.example.kotlintask3.ui


import com.example.kotlintask3.api.Item

interface UiFunction {

    fun downloadImage(url: String, imageName: String)

    fun updateName(id: String, item: Item, oldName: String, newName: String)

}