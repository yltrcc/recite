package com.yltrcc.app.recite.entity

data class QuestionCategoryEntity(
    val categoryId: Long = 0,
    var categoryName: String = "",
    val categoryUrl: String = "",
    val categoryDescribe: String = "",
    val isTop: Int = 0,
    val upperCategoryId: Int = 0,
    val isFinal: Int = 0
)