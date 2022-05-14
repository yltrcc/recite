package com.yltrcc.app.recite.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionListEntity(
    val subCategoryName: String,
    val subCategoryId: Int,
    val data: List<QuestionEntity> ,
): Parcelable