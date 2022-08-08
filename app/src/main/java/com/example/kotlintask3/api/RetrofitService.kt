package com.example.kotlintask3.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path


interface RetrofitService {
    val id: String

    @GET("items")
    fun getData(): Call<List<Item>>

    @GET("items/{id}")
    fun getSelectedItemData(@Path("id") id: String): Call<Item>

    @PATCH("items/{id}")
    fun updateName(@Path("id") id: String, @Body item: Item): Call<Item>



}