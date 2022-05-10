package com.yltrcc.app.recite.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionV3ListEntity(
    val categoryName: String,
    val data: List<QuestionV2ListEntity> ,
): Parcelable
