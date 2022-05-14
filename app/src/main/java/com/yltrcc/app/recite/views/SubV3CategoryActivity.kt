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
import com.yltrcc.app.recite.adapter.CategoryV3MainAdapter
import com.yltrcc.app.recite.adapter.SubCategoryMainAdapter
import com.yltrcc.app.recite.adapter.SubCategoryMoreAdapter
import com.yltrcc.app.recite.entity.*
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.*

class SubV3CategoryActivity : AppCompatActivity() {

    private var queryUrlAll =
        ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYALLV3_BY_CATEGORY_ID
    private var queryAllCategory = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYALLV3
    private var queryQuestion = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUESTION_BY_SUB
    private lateinit var data: List<QuestionV3ListEntity>
    private lateinit var questionData: List<QuestionEntity>
    private lateinit var ctx: Context
    private var categoryId: Long = 0
    var mainAdapter: SubCategoryMainAdapter? = null
    var mainV2Adapter: CategoryMainAdapter? = null
    var mainV3Adapter: CategoryV3MainAdapter? = null
    var moreAdapter: SubCategoryMoreAdapter? = null
    private var mainlist: ListView? = null
    private var mainV2list: ListView? = null
    private var mainV3list: ListView? = null
    private var morelist: ListView? = null
    private var content: String = ""
    private var contentStr: String = ""

    //记录第一栏点击位置
    private var headPosition: Int = 0

    //记录第二栏点击位置
    private var firPosition: Int = 0

    //记录第三栏点击位置
    private var secPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_v3_category)

        ctx = this
        //判断本地是否有内存
        //如果sp有数据
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("SubV3CategoryActivity", MODE_PRIVATE)
        content = sharedPreferences.getString("content", "").toString();
        contentStr = sharedPreferences.getString("contentStr", "").toString();
        if (content.length > 0) {
            val gson = Gson()
            data = gson.fromJson(content, Array<QuestionV3ListEntity>::class.java).toList()
            initView()
        } else {
            queryByCategory()
        }
    }

    //HTTP GET
    fun queryByCategory() = GlobalScope.launch(Dispatchers.Main) {

        val progressDialog: ProgressDialog =
            ProgressDialog.show(ctx, "请稍等...", "大数据量，正获取数据中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET2(queryAllCategory, 30L) }.await()
            ?.let {
                val result = Gson().fromJson<Response<QuestionV3ListEntity>>(
                    it,
                    object : TypeToken<Response<QuestionV3ListEntity>>() {}.type
                )
                data = result.data
                val sharedPreferences: SharedPreferences =
                    getSharedPreferences("SubV3CategoryActivity", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("content", Gson().toJson(data))
                editor.putString("contentStr", it)
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


    /**
     * 通过子分类 拉取 面试题
     */
    fun queryBySubCategory(subCategoryId: Int, position:Int) = GlobalScope.launch(Dispatchers.Main) {

        val progressDialog: ProgressDialog = ProgressDialog.show(ctx, "请稍等...", "获取数据中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) {
            http.httpGET2(
                queryQuestion + "?subCategoryId=" + subCategoryId,
                30L
            )
        }.await()
            ?.let {
                val result = Gson().fromJson<Response<QuestionListEntity>>(
                    it,
                    object : TypeToken<Response<QuestionListEntity>>() {}.type
                )
                questionData = result.data[0].data
                if (data.size > 0) {
                    if (questionData.size > 0) {
                        initAdapter(questionData)
                    }
                    mainAdapter!!.setSelectItem(position)
                    mainAdapter!!.notifyDataSetChanged()
                    secPosition = position
                }else {
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
        mainlist = findViewById<View>(R.id.v3_mainlist) as ListView
        mainV2list = findViewById<View>(R.id.v3_main2list) as ListView
        mainV3list = findViewById<View>(R.id.v3_main3list) as ListView
        morelist = findViewById<View>(R.id.v3_morelist) as ListView

        mainV3Adapter = CategoryV3MainAdapter(
            this@SubV3CategoryActivity,
            data
        )
        mainV3Adapter!!.setSelectItem(0)
        mainV3list!!.setAdapter(mainV3Adapter)
        mainV3list!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            initV3Adapter(data.get(position).data)
            mainV3Adapter!!.setSelectItem(position)
            mainV3Adapter!!.notifyDataSetChanged()
            this.headPosition = position
            this.firPosition = 0
            this.secPosition = 0
        })
        mainV3list!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)



        mainV2Adapter = CategoryMainAdapter(
            this@SubV3CategoryActivity,
            data[0].data
        )
        mainV2Adapter!!.setSelectItem(0)
        mainV2list!!.setAdapter(mainV2Adapter)
        mainV2list!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            initV2Adapter(data.get(headPosition).data.get(position).data)
            mainV2Adapter!!.setSelectItem(position)
            mainV2Adapter!!.notifyDataSetChanged()
            this.firPosition = position
            this.secPosition = 0
        })
        mainV2list!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)

        mainAdapter = SubCategoryMainAdapter(
            this@SubV3CategoryActivity,
            data[0].data[0].data
        )
        mainAdapter!!.setSelectItem(0)
        mainlist!!.setAdapter(mainAdapter)
        mainlist!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            queryBySubCategory(data.get(headPosition).data.get(firPosition).data.get(position).subCategoryId, position)

        })
        mainlist!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)


        // 一定要设置这个属性，否则ListView不会刷新
        initV3Adapter(data[0].data)
        morelist!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            moreAdapter?.setSelectItem(position)
            moreAdapter?.notifyDataSetChanged()
            println("点击了第 " + headPosition + "," + firPosition + "," + secPosition + "," + position + "个位置")
            //# 跳转端
            val intent = Intent()
            intent.setClass(ctx, MarkdownActivity::class.java)
            intent.putExtra("content", questionData.get(position).articleContentMd)
            ctx.startActivity(intent)
        })
    }

    private fun initV3Adapter(lists: List<QuestionV2ListEntity>) {

        mainV2Adapter = CategoryMainAdapter(
            this@SubV3CategoryActivity,
            lists
        )
        mainV2list?.setAdapter(mainV2Adapter)
        mainV2Adapter!!.notifyDataSetChanged()


        mainAdapter = SubCategoryMainAdapter(
            this@SubV3CategoryActivity,
            lists[0].data
        )
        mainlist?.setAdapter(mainAdapter)
        mainAdapter!!.notifyDataSetChanged()

/*        moreAdapter =
            SubCategoryMoreAdapter(this, lists[0].data[0].data)
        morelist?.setAdapter(moreAdapter)
        moreAdapter!!.notifyDataSetChanged()*/
    }


    private fun initV2Adapter(lists: List<QuestionListEntity>) {

        mainAdapter = SubCategoryMainAdapter(
            this@SubV3CategoryActivity,
            lists
        )
        mainlist?.setAdapter(mainAdapter)
        mainAdapter!!.notifyDataSetChanged()

//        moreAdapter =
//            SubCategoryMoreAdapter(this, lists.get(0).data)
//        morelist?.setAdapter(moreAdapter)
//        moreAdapter!!.notifyDataSetChanged()
    }

    private fun initAdapter(lists: List<QuestionEntity>) {
        moreAdapter =
            SubCategoryMoreAdapter(this, lists)
        morelist?.setAdapter(moreAdapter)
        moreAdapter!!.notifyDataSetChanged()
    }
}