package com.yltrcc.app.recite

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.adapter.ClassifyMainAdapter
import com.yltrcc.app.recite.adapter.ClassifyMoreAdapter
import com.yltrcc.app.recite.entity.QuestionEntity
import com.yltrcc.app.recite.entity.QuestionListEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {

    private var queryUrl = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERY_BY_CATEGORY_ID
    private lateinit var data: List<QuestionListEntity>
    private lateinit var ctx: Context
    private var categoryId:Long = 0
    var mainAdapter: ClassifyMainAdapter? = null
    var moreAdapter: ClassifyMoreAdapter? = null
    private var mainlist: ListView? = null
    private var morelist: ListView? = null

    //记录右侧点击位置
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classify)

        //接收 intent
        val categoryName = intent.getStringExtra("categoryName")
        categoryId = intent.getLongExtra("categoryId", 0)
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
                val result = Gson().fromJson<Response<QuestionListEntity>>(
                    it,
                    object : TypeToken<Response<QuestionListEntity>>() {}.type
                )
                print(result.data)
                data = result.data
                if (data.size > 0) {
                    initView()
                }else {
                    val builder = AlertDialog.Builder(ctx)
                    builder.setTitle("尊敬的用户")
                    builder.setMessage("暂无后台数据，请联系管理员添加")
                    builder.setPositiveButton("确定") {
                            dialog, which -> finish() }

                    val alert = builder.create()
                    alert.show()
                }
                progressDialog.dismiss();//去掉加载框
            }
    }

    private fun initView() {
        mainlist = findViewById<View>(R.id.classify_mainlist) as ListView
        morelist = findViewById<View>(R.id.classify_morelist) as ListView
        mainAdapter = ClassifyMainAdapter(this@ListActivity, data)
        mainAdapter!!.setSelectItem(0)
        mainlist!!.setAdapter(mainAdapter)
        mainlist!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            initAdapter(data.get(position).data)
            mainAdapter!!.setSelectItem(position)
            mainAdapter!!.notifyDataSetChanged()
            this.position = position
        })
        mainlist!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)
        // 一定要设置这个属性，否则ListView不会刷新
        initAdapter(data.get(0).data)
        morelist!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            moreAdapter?.setSelectItem(position)
            moreAdapter?.notifyDataSetChanged()
            println("点击了第 " + position + "个位置")
            val entity:QuestionListEntity = data.get(this.position)
            //Toast.makeText(ctx, "Clicked item :" + " " + position + " id: " + id + " name: " + entity.articleTitle, Toast.LENGTH_SHORT).show()
            //# 跳转端
            val intent = Intent()
            intent.setClass(ctx, QuestionDetailsActivity::class.java)
            intent.putExtra("content", entity.data.get(position).articleContent)
            intent.putExtra("title", entity.data.get(position).articleTitle)
            intent.putExtra("categoryId", categoryId)
            ctx.startActivity(intent)
        })
    }

    private fun initAdapter(lists: List<QuestionEntity>) {
        moreAdapter = ClassifyMoreAdapter(this, lists)
        morelist?.setAdapter(moreAdapter)
        moreAdapter!!.notifyDataSetChanged()
    }
}