package com.yltrcc.app.recite.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.entity.QuestionCategoryEntity
import com.yltrcc.app.recite.entity.QuestionEntity

class QuestionAdapter(activity: Activity, val resourceId: Int, data: List<QuestionEntity>) :
    ArrayAdapter<QuestionEntity>(activity, resourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        val itemTitle: TextView = view.findViewById(R.id.tv_question_title)
        val question = getItem(position)//获取当前项得Fruit实例
        if (question != null) {
            //itemImage.setImageResource("")
            itemTitle.text = question.articleTitle
        }
        return view
    }
}