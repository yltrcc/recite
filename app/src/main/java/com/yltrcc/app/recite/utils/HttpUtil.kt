package com.yltrcc.app.recite.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.connection
import java.util.concurrent.TimeUnit

class HttpUtil {
    fun httpGET1(url : String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string()
        return body
    }

    fun httpGET2(url: String, timeout:Long, unit: TimeUnit): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = OkHttpClient().newBuilder()
            .connectTimeout(timeout, unit)
            .readTimeout(timeout, unit)
            .build().newCall(request).execute()
        val body = response.body?.string()
        return body
    }
}
