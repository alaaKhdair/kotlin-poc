package com.example.kotlintask3.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {


    companion object {
        private const val BASE_URL = "https://62b19d03c7e53744afbd0b3b.mockapi.io/api/v1/"

        private val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        val retrofitService: RetrofitService by lazy {
            retrofit.create(RetrofitService::class.java)
        }
    }
}

