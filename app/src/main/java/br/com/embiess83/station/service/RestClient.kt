package br.com.embiess83.station.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RestClient {
    companion object {
        fun getClient(): Retrofit {
            val client = OkHttpClient.Builder().build()

            return Retrofit.Builder()
                .baseUrl("https://station-api-dj49k.ondigitalocean.app")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }
}