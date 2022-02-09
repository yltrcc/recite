package com.yltrcc.app.recite.entity

data class Response<T>(
    val status: Int,
    val code: Int,
    val message: String,
    val data: List<T>
)



