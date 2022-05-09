package com.yltrcc.app.recite.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionListEntity(
    val categoryName: String,
    val data: List<QuestionEntity> ,
): Parcelable