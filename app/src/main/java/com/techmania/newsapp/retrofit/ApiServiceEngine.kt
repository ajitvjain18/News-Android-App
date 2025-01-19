package com.techmania.newsapp.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceEngine {

    private const val BASEURL = "https://newsapi.org/v2/"


    private val okHttp = OkHttpClient.Builder()

    private val builder =
        Retrofit.Builder().baseUrl(BASEURL).addConverterFactory(GsonConverterFactory.create())
            .client(
                okHttp.build()
            )

    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}