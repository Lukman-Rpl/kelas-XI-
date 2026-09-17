package com.example.sakubijak

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    const val BASE_URL = "http://192.168.137.1:8000/api/"

    @Volatile
    private var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: buildRetrofit(context.applicationContext).also { apiService = it }
        }
    }

    private fun buildRetrofit(context: Context): ApiService {
        // Menggunakan PreferenceManager konsisten dengan LoginActivity & SplashActivity
        val prefManager = PreferenceManager(context)

        val authInterceptor = Interceptor { chain ->
            // Mengambil token menggunakan fungsi getToken() dari PreferenceManager
            val token = prefManager.getToken()

            val requestBuilder = chain.request().newBuilder()
                .header("Accept", "application/json")

            if (!token.isNullOrEmpty()) {
                val authHeaderValue = if (token.startsWith("Bearer ")) token else "Bearer $token"
                requestBuilder.header("Authorization", authHeaderValue)
            }

            chain.proceed(requestBuilder.build())
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}