package com.yltrcc.app.recite.views

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Contacts
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.adapter.CategoryMainAdapter
import com.yltrcc.app.recite.adapter.SubCategoryMainAdapter
import com.yltrcc.app.recite.adapter.SubCategoryMoreAdapter
import com.yltrcc.app.recite.entity.QuestionEntity
import com.yltrcc.app.recite.entity.QuestionListEntity
import com.yltrcc.app.recite.entity.QuestionV2ListEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.*

class SubV2CategoryActivity : AppCompatActivity() {

    private var queryUrl = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYV2_BY_CATEGORY_ID
    private var queryUrlAll =
        ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYALLV2_BY_CATEGORY_ID
    private lateinit var data: List<QuestionV2ListEntity>
    private lateinit var ctx: Context
    private var categoryId: Long = 0
    var mainAdapter: SubCategoryMainAdapter? = null
    var mainV2Adapter: CategoryMainAdapter? = null
    var moreAdapter: SubCategoryMoreAdapter? = null
    private var mainlist: ListView? = null
    private var mainV2list: ListView? = null
    private var morelist: ListView? = null
    private var content: String = ""
    private var contentStr: String = ""

    //记录第一栏点击位置
    private var firPosition: Int = 0

    //记录第二栏点击位置
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_v2_category)

        //接收 intent
        val categoryName = intent.getStringExtra("categoryName")
        categoryId = intent.getLongExtra("categoryId", 9)
        queryUrl = queryUrl + "?categoryId=" + categoryId
        ctx = this
        //判断本地是否有内存
        //如果sp有数据
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("SubV2CategoryActivity", MODE_PRIVATE)
        content = sharedPreferences.getString("content", null).toString();
        contentStr = sharedPreferences.getString("contentStr", null).toString();
        if (content.length > 0) {
            val gson = Gson()
            data = gson.fromJson(content, Array<QuestionV2ListEntity>::class.java).toList()
            initView()
            //更新sp
            job()
        } else {
            queryByCateogry()
        }
    }

    /**
     * 异步去请求请求数据然后更新到 sp
     */
    //异步起一个线程去更新content
    fun job() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET2(queryUrlAll, 30L) }.await()
            ?.let {
                if (!contentStr.equals(it)) {
                    val result = Gson().fromJson<Response<QuestionV2ListEntity>>(
                        it,
                        object : TypeToken<Response<QuestionV2ListEntity>>() {}.type
                    )
                    //表示数据不一致 需要更新
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("SubV2CategoryActivity", MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("content", Gson().toJson(result.data))
                    editor.putString("contentStr", it)
                    editor.apply()
                }
            }
    }

    //HTTP GET
    fun queryByCateogry() = GlobalScope.launch(Dispatchers.Main) {

        val progressDialog: ProgressDialog = ProgressDialog.show(ctx, "请稍等...", "获取数据中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET1(queryUrl) }.await()
            ?.let {
                val result = Gson().fromJson<Response<QuestionV2ListEntity>>(
                    it,
                    object : TypeToken<Response<QuestionV2ListEntity>>() {}.type
                )
                data = result.data
                val sharedPreferences: SharedPreferences =
                    getSharedPreferences("SubV2CategoryActivity", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("content", Gson().toJson(data))
                editor.apply()
                if (data.size > 0) {
                    initView()
                } else {
                    val builder = AlertDialog.Builder(ctx)
                    builder.setTitle("尊敬的用户")
                    builder.setMessage("暂无后台数据，请联系管理员添加")
                    builder.setPositiveButton("确定") { dialog, which -> finish() }

                    val alert = builder.create()
                    alert.show()
                }
                progressDialog.dismiss();//去掉加载框
            }
    }

    private fun initView() {
        mainlist = findViewById<View>(R.id.v2_mainlist) as ListView
        mainV2list = findViewById<View>(R.id.v2_main2list) as ListView
        morelist = findViewById<View>(R.id.v2_morelist) as ListView

        mainV2Adapter = CategoryMainAdapter(
            this@SubV2CategoryActivity,
            data
        )
        mainV2Adapter!!.setSelectItem(0)
        mainV2list!!.setAdapter(mainV2Adapter)
        mainV2list!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            initV2Adapter(data.get(position).data)
            mainV2Adapter!!.setSelectItem(position)
            mainV2Adapter!!.notifyDataSetChanged()
            this.firPosition = position
        })
        mainV2list!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)

        mainAdapter = SubCategoryMainAdapter(
            this@SubV2CategoryActivity,
            data[0].data
        )
        mainAdapter!!.setSelectItem(0)
        mainlist!!.setAdapter(mainAdapter)
        mainlist!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            initAdapter(data[firPosition].data.get(position).data)
            mainAdapter!!.setSelectItem(position)
            mainAdapter!!.notifyDataSetChanged()
            this.position = position
        })
        mainlist!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)


        // 一定要设置这个属性，否则ListView不会刷新
        initV2Adapter(data.get(0).data)
        initAdapter(data[0].data.get(0).data)
        morelist!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            moreAdapter?.setSelectItem(position)
            moreAdapter?.notifyDataSetChanged()
            println("点击了第 " + position + "个位置")
            val entity: QuestionListEntity = data[0].data.get(this.position)
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


    private fun initV2Adapter(lists: List<QuestionListEntity>) {

        mainAdapter = SubCategoryMainAdapter(
            this@SubV2CategoryActivity,
            lists
        )
        mainlist?.setAdapter(mainAdapter)
        mainAdapter!!.notifyDataSetChanged()

        moreAdapter =
            SubCategoryMoreAdapter(this, lists.get(0).data)
        morelist?.setAdapter(moreAdapter)
        moreAdapter!!.notifyDataSetChanged()
    }

    private fun initAdapter(lists: List<QuestionEntity>) {
        moreAdapter =
            SubCategoryMoreAdapter(this, lists)
        morelist?.setAdapter(moreAdapter)
        moreAdapter!!.notifyDataSetChanged()
    }
}