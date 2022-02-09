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

class CategoryAdapter(activity: Activity, val resourceId: Int, data: List<QuestionCategoryEntity>) :
    ArrayAdapter<QuestionCategoryEntity>(activity, resourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        val itemImage: ImageView = view.findViewById(R.id.iv_image)
        val itemContent: TextView = view.findViewById(R.id.tv_content)
        val category = getItem(position)//获取当前项得Fruit实例
        if (category != null) {
            //itemImage.setImageResource("")
            itemContent.text = category.categoryName
        }
        return view
    }
}