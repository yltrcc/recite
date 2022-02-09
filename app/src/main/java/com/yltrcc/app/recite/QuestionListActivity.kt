package com.yltrcc.app.recite

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.adapter.CategoryAdapter
import com.yltrcc.app.recite.adapter.QuestionAdapter
import com.yltrcc.app.recite.entity.QuestionCategoryEntity
import com.yltrcc.app.recite.entity.QuestionEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class QuestionListActivity : AppCompatActivity() {

    private var queryUrl = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERY_BY_CATEGORY
    private lateinit var data: List<QuestionEntity>
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_list)

        //接收 intent
        val categoryName = intent.getStringExtra("categoryName")
        val categoryId = intent.getLongExtra("categoryId", 0)
        queryUrl = queryUrl + "?categoryId=" + categoryId
        ctx = this
        queryByCateogry()

    }

    //HTTP GET
    fun queryByCateogry() = GlobalScope.launch(Dispatchers.Main) {

        var progressDialog: ProgressDialog = ProgressDialog.show(ctx, "请稍等...", "获取数据中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET1(queryUrl) }.await()
            ?.let {
                print(it)
                val result = Gson().fromJson<Response<QuestionEntity>>(
                    it,
                    object : TypeToken<Response<QuestionEntity>>() {}.type
                )
                print(result.data)
                data = result.data
                val adapter = QuestionAdapter(ctx as Activity, R.layout.question_item, data)
                val listview: ListView = findViewById(R.id.lv_question_list)
                listview.adapter = adapter
                listview.setOnItemClickListener { parent, view, position, id ->
                    val entity:QuestionEntity = data[position]
                    Toast.makeText(ctx, "Clicked item :" + " " + position + " id: " + id + " name: " + entity.articleTitle, Toast.LENGTH_SHORT).show()

                }
                progressDialog.dismiss();//去掉加载框
            }
    }
}