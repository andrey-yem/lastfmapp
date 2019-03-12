package com.example.andrey.lastfmapp.api

import okhttp3.Interceptor
import okhttp3.Response

class LastFmInterceptor(private val apiKey: String) :
        Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url().newBuilder()
                .addQueryParameter("api_key", apiKey)
                .addQueryParameter("format", "json")
                .build()

        val newRequest = request.newBuilder()
                .url(url)
                .build()

        return chain.proceed(newRequest)
    }
}