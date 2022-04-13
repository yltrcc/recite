package com.yltrcc.app.recite

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.adapter.CategoryAdapter
import com.yltrcc.app.recite.entity.QuestionCategoryEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CategoryActivity : AppCompatActivity() {

    private var PAGE_COUNT = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_COUNT
    private var CATEGORIES = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_CATEGORY
    private var count: Int = 2
    private lateinit var data: MutableList<QuestionCategoryEntity>
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        ctx = this
        initView() //初始化列表数据
    }

    private fun initView() {
        updateApp()
        getCount()
        getCategory()
        val btnHomepage:Button = findViewById(R.id.category_btn_homepage)
        btnHomepage.setOnClickListener(object :View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, MainActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })
    }

    private fun updateApp() = GlobalScope.launch(Dispatchers.Main) {
        //判断当前版本信息，更新APP
        val manager: PackageManager = ctx.packageManager
        val info: PackageInfo = manager.getPackageInfo(ctx.packageName, 0)
        var code: Long= info.longVersionCode

        /*val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET1(PAGE_COUNT) }.await()
            ?.let {
                print(it)
                count = it.toInt()
            }*/
    }

    //HTTP GET
    fun getCount() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET1(PAGE_COUNT) }.await()
            ?.let {
                print(it)
                count = it.toInt()
            }
    }

    //HTTP GET
    fun getCategory() = GlobalScope.launch(Dispatchers.Main) {

        var progressDialog: ProgressDialog = ProgressDialog.show(ctx, "请稍等...", "获取数据中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET1(CATEGORIES + "?isTop=true") }.await()
            ?.let {
                print(it)
                val result = Gson().fromJson<Response<QuestionCategoryEntity>>(
                    it,
                    object : TypeToken<Response<QuestionCategoryEntity>>() {}.type
                )
                print(result.data)
                val randomEntity:QuestionCategoryEntity = QuestionCategoryEntity()
                randomEntity.categoryName = "随机来一题"
                data = mutableListOf<QuestionCategoryEntity>()
                data.add(randomEntity)
                data.addAll(result.data)
                val adapter = CategoryAdapter(ctx as Activity, R.layout.category_item, data)
                val listview: ListView = findViewById(R.id.lv_category)
                listview.adapter = adapter
                listview.setOnItemClickListener { parent, view, position, id ->
                    if (position == 0) {
                        val intent = Intent()
                        intent.setClass(this@CategoryActivity, QuestionDetailsActivity::class.java)
                        intent.putExtra("count", count)
                        startActivity(intent)
                    }else {
                        val entity:QuestionCategoryEntity = data[position]
                        //Toast.makeText(ctx, "Clicked item :" + " " + position + " id: " + id + " name: " + entity.categoryName, Toast.LENGTH_SHORT).show()
                        if (entity.isFinal == 1) {
                            //# 跳转端
                            val intent = Intent()
                            intent.setClass(ctx, ListActivity::class.java)
                            intent.putExtra("categoryName", entity.categoryName)
                            intent.putExtra("categoryId", entity.categoryId)
                            ctx.startActivity(intent)
                        }else {
                            //# 跳转端
                            val intent = Intent()
                            intent.setClass(ctx, SecondCategoryActivity::class.java)
                            intent.putExtra("categoryName", entity.categoryName)
                            intent.putExtra("categoryId", entity.categoryId)
                            ctx.startActivity(intent)
                        }

                    }
                }
                progressDialog.dismiss();//去掉加载框
            }
    }

    @Override
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}