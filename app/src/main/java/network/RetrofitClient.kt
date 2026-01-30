package com.example.workhive.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Put your Replit backend base URL here (end with /)
    // Example: "https://your-repl-url.repl.co/"
    const val BASE_URL = "https://a383f496-9817-4a1e-9b5b-2cd5bc79bf44-00-34hbznmbc9l1g.pike.replit.dev/"

    val api: ApiService by lazy {
        // Logging interceptor - avoid overly verbose in production
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
