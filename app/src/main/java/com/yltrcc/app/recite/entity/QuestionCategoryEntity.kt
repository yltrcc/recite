package com.yltrcc.app.recite.entity

data class QuestionCategoryEntity(
    val categoryId: Long,
    val categoryName: String,
    val categoryUrl: String,
    val categoryDescribe: String,
    val isTop: Int,
    val upperCategoryId: Int,
    val isFinal: Int
)