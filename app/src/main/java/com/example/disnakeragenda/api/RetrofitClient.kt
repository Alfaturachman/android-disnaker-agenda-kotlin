package com.example.disnakeragenda.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val ip: String = "192.168.0.106"

    private val BASE_URL = "http://$ip/disnaker_agenda_api/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}