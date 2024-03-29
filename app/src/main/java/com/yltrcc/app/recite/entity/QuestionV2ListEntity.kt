package com.yltrcc.app.recite.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionV2ListEntity(
    val categoryName: String,
    val categoryId: Int,
    val data: List<QuestionListEntity>
): Parcelable
