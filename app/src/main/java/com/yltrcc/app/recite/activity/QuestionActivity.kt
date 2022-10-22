package com.yltrcc.app.recite.activity

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.*
import com.yltrcc.app.recite.adapter.*
import com.yltrcc.app.recite.entity.*
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import com.yltrcc.app.recite.utils.StatusBarUtils
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.TimeUnit


class QuestionActivity : AppCompatActivity() {

    private val TAG = QuestionActivity::class.java.simpleName
    private lateinit var ctx: Context
    private var queryAllCategory = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYALLV3
    private var queryQuestion = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUESTION_BY_SUB
    private lateinit var data: List<QuestionV3ListEntity>
    private lateinit var questionData: MutableList<QuestionEntity>
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
    private lateinit var sharedPreferences: SharedPreferences

    //记录第一栏点击位置
    private var headPosition: Int = 0

    //记录第二栏点击位置
    private var firPosition: Int = 0

    //记录第三栏点击位置
    private var secPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        StatusBarUtils.setStatusBar(window, R.color.colorPrimary)
        ctx = this
        //判断本地是否有内存
        //如果sp有数据
        sharedPreferences = getSharedPreferences("CategoryActivity", MODE_PRIVATE)
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
        async(Dispatchers.Default) {
            http.httpGET2(
                queryAllCategory,
                1L, TimeUnit.SECONDS
            )
        }.await()
            ?.let {
                val result = Gson().fromJson<Response<QuestionV3ListEntity>>(
                    it,
                    object : TypeToken<Response<QuestionV3ListEntity>>() {}.type
                )
                data = result.data
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("content", Gson().toJson(data))
                editor.putString("contentStr", it)
                editor.apply()
                if (data.size > 0) {
                    initView()
                } else {
                    Toast.makeText(ctx, "暂无数据", Toast.LENGTH_SHORT).show()
                }
                progressDialog.dismiss();//去掉加载框
            }
    }


    /**
     * 通过子分类 拉取 面试题
     */
    fun queryBySubCategory(subCategoryId: Int, headPosition: Int, firPosition: Int, position: Int) =
        GlobalScope.launch(Dispatchers.Main) {

            val progressDialog: ProgressDialog =
                ProgressDialog.show(ctx, "请稍等...", "获取数据中...", true)

            val http = HttpUtil()
            supervisorScope {
                try {
                    //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
                    async(Dispatchers.Default) {
                        http.httpGET2(
                            queryQuestion + "?subCategoryId=" + subCategoryId,
                            2000L, TimeUnit.MILLISECONDS
                        )
                    }.await()
                        ?.let {
                            val result = Gson().fromJson<Response<QuestionListEntity>>(
                                it,
                                object : TypeToken<Response<QuestionListEntity>>() {}.type
                            )
                            questionData = result.data[0].data.toMutableList()
                            if (questionData.size > 0) {
                                initAdapter(questionData)
                                mainAdapter!!.setSelectItem(position)
                                mainAdapter!!.notifyDataSetChanged()
                                secPosition = position
                                //刷新本地缓存
                                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                editor.putString(""+headPosition+firPosition+position, it)
                                editor.apply()
                            } else {
                                Toast.makeText(ctx, "暂无数据", Toast.LENGTH_SHORT).show()
                            }
                            progressDialog.dismiss();//去掉加载框
                        }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.toString())
                    //尝试本地加载
                    val string =
                        sharedPreferences.getString("" + headPosition + firPosition + position, "")
                    if (string != null && string.length > 0) {
                        val result = Gson().fromJson<Response<QuestionListEntity>>(
                            string,
                            object : TypeToken<Response<QuestionListEntity>>() {}.type
                        )
                        questionData = result.data[0].data.toMutableList()
                        initAdapter(questionData)
                        mainAdapter!!.setSelectItem(position)
                        mainAdapter!!.notifyDataSetChanged()
                        secPosition = position
                    }else {
                        Toast.makeText(ctx, "暂无数据", Toast.LENGTH_SHORT).show()
                    }
                    progressDialog.dismiss();//去掉加载框
                }
            }

        }


    private fun initView() {

        mainlist = findViewById<View>(R.id.ca_mainlist) as ListView
        mainV2list = findViewById<View>(R.id.ca_main2list) as ListView
        mainV3list = findViewById<View>(R.id.ca_main3list) as ListView
        morelist = findViewById<View>(R.id.ca_morelist) as ListView

        mainV3Adapter = CategoryV3MainAdapter(
            this@QuestionActivity,
            data
        )
        mainV3Adapter!!.setSelectItem(0)
        mainV3list!!.setAdapter(mainV3Adapter)
        mainV3list!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            this.headPosition = position
            this.firPosition = 0
            this.secPosition = 0
            initV3Adapter(data.get(position).data)
            mainV3Adapter!!.setSelectItem(position)
            mainV3Adapter!!.notifyDataSetChanged()

        })
        mainV3list!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)



        mainV2Adapter = CategoryMainAdapter(
            this@QuestionActivity,
            data[0].data
        )
        mainV2Adapter!!.setSelectItem(0)
        mainV2list!!.setAdapter(mainV2Adapter)
        mainV2list!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            this.firPosition = position
            this.secPosition = 0
            initV2Adapter(data.get(headPosition).data.get(position).data)
            mainV2Adapter!!.setSelectItem(position)
            mainV2Adapter!!.notifyDataSetChanged()

        })
        mainV2list!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)

        mainAdapter = SubCategoryMainAdapter(
            this@QuestionActivity,
            data[0].data[0].data
        )
        mainAdapter!!.setSelectItem(0)
        mainlist!!.setAdapter(mainAdapter)
        mainlist!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            queryBySubCategory(
                data.get(headPosition).data.get(firPosition).data.get(position).subCategoryId,
                headPosition, firPosition, position
            )

        })
        mainlist!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)


        // 一定要设置这个属性，否则ListView不会刷新
        val index = intent.getStringExtra("index")
        if (index != null) {
            headPosition = index.toInt()
            initV3Adapter(data.get(headPosition).data)
            mainV3Adapter!!.setSelectItem(headPosition)
            mainV3Adapter!!.notifyDataSetChanged()
            val taskCategorySP: SharedPreferences =
                getSharedPreferences("taskCategory", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = taskCategorySP.edit()
            if (headPosition + 1 < data.size) {
                editor.putString("question", (headPosition + 1).toString() + "@" + data.get(headPosition + 1).categoryName)
            }else {
                editor.putString("question", "0@" + data.get(0).categoryName)
            }
            editor.apply()
        }else {
            initV3Adapter(data[0].data)
        }
        morelist!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            moreAdapter?.setSelectItem(position)
            moreAdapter?.notifyDataSetChanged()
            println("点击了第 " + headPosition + "," + firPosition + "," + secPosition + "," + position + "个位置")
            //# 跳转端
            val intent = Intent()
            intent.setClass(ctx, MarkdownActivity::class.java)
            intent.putExtra("content", questionData.get(position).articleContentMd)
            intent.putExtra("id", questionData.get(position).id)
            intent.putExtra("subCategoryId", data.get(headPosition).data.get(firPosition).data.get(secPosition).subCategoryId)
            intent.putExtra("subCategoryName", data.get(headPosition).data.get(firPosition).data.get(secPosition).subCategoryName)
            intent.putExtra("categoryName", data.get(headPosition).data.get(firPosition).categoryName)
            intent.putExtra("categoryId", data.get(headPosition).data.get(firPosition).categoryId)
            ctx.startActivity(intent)
        })

        val btnHomepage: Button = findViewById(R.id.category_btn_homepage)
        val btnAlgorithm: Button = findViewById(R.id.category_btn_algorithm)
        val btnArticle: Button = findViewById(R.id.category_btn_article)
        btnHomepage.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的首页页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, MainActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })

        btnAlgorithm.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, AlgorithmActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })
        btnArticle.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, ArticleActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })
    }

    private fun initV3Adapter(lists: List<QuestionV2ListEntity>) {

        mainV2Adapter = CategoryMainAdapter(
            this@QuestionActivity,
            lists
        )
        mainV2list?.setAdapter(mainV2Adapter)
        mainV2Adapter!!.notifyDataSetChanged()


        mainAdapter = SubCategoryMainAdapter(
            this@QuestionActivity,
            lists[0].data
        )
        mainlist?.setAdapter(mainAdapter)
        mainAdapter!!.notifyDataSetChanged()

//       moreAdapter =
//            SubCategoryMoreAdapter(this, lists[0].data[0].data)
//        morelist?.setAdapter(moreAdapter)
//        moreAdapter!!.notifyDataSetChanged()
        queryBySubCategory(
            data.get(headPosition).data.get(firPosition).data.get(0).subCategoryId,
            headPosition, firPosition, 0
        )

    }


    private fun initV2Adapter(lists: List<QuestionListEntity>) {

        mainAdapter = SubCategoryMainAdapter(
            this@QuestionActivity,
            lists
        )
        mainlist?.setAdapter(mainAdapter)
        mainAdapter!!.notifyDataSetChanged()

//        moreAdapter =
//            SubCategoryMoreAdapter(this, lists.get(0).data)
//        morelist?.setAdapter(moreAdapter)
//        moreAdapter!!.notifyDataSetChanged()
        queryBySubCategory(
            data.get(headPosition).data.get(firPosition).data.get(0).subCategoryId,
            headPosition, firPosition, 0
        )
    }

    private fun initAdapter(lists: List<QuestionEntity>) {
        moreAdapter =
            SubCategoryMoreAdapter(this, lists)
        morelist?.setAdapter(moreAdapter)
        moreAdapter!!.notifyDataSetChanged()
    }

    @Override
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}