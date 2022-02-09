package com.yltrcc.app.recite.entity

import java.util.*

data class QuestionEntity(
    val id: Long,
    val userId: Long,
    val articleContent: String,
    val articleContentMd: String,
    val articleNewstime: Date,
    val articleStatus: Int,
    val articleSummary: String,
    val articleThumbnail: String,
    val articleTitle: String,
    val articleType: Int,
    val articlePost: String,
    val articleComment: Int,
    val articleUpdatetime: Date,
    val articleUrl: String,
    val articleViews: Long,
    val categoryId: Long
)