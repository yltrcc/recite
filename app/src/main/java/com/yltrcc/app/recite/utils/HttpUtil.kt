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

    fun httpGET2(url: String, timeout:Long): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = OkHttpClient().newBuilder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .build().newCall(request).execute()
        val body = response.body?.string()
        return body
    }
}
