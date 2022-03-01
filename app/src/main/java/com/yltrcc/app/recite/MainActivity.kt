package com.yltrcc.app.recite

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.yltrcc.app.recite.entity.QuestionCategoryEntity
import com.yltrcc.app.recite.utils.ConstantUtils


class MainActivity : AppCompatActivity() {

    private var PAGE_COUNT = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_COUNT
    private var CATEGORIES = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_CATEGORY
    private var count: Int = 2
    private lateinit var data: MutableList<QuestionCategoryEntity>
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ctx = this
        init() //初始化列表数据

        //如果sp有数据
        val sharedPreferences:SharedPreferences = getSharedPreferences("questionDetails", MODE_PRIVATE)

        val content: String? = sharedPreferences.getString("content", null)
        val title: String? = sharedPreferences.getString("title", null)


        val clickHistory:TextView = findViewById(R.id.main_tv_click_history)
        val btnCategory:Button = findViewById(R.id.main_btn_category)

        if (content != null) {
            val text:String = "您最近一次点击了：" + (title?.slice(0..17) ?: String) + "..."
            clickHistory.setText(text)
        }
        clickHistory.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面
                val intent = Intent()
                intent.setClass(ctx, QuestionDetailsActivity::class.java)
                intent.putExtra("content", content)
                intent.putExtra("title", title)
                ctx.startActivity(intent)
            }
        })
        btnCategory.setOnClickListener(object :View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面
                val intent = Intent()
                intent.setClass(ctx, CategoryActivity::class.java)
                ctx.startActivity(intent)
            }
        })
    }

    private fun init() {

    }


}