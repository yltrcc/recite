package com.yltrcc.app.recite.activity.study

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.*
import com.yltrcc.app.recite.adapter.*
import com.yltrcc.app.recite.entity.*
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HelpUtils
import com.yltrcc.app.recite.utils.HttpUtil
import com.yltrcc.app.recite.utils.TabBarUtils
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class ArticleActivity : AppCompatActivity() {

    private val TAG = ArticleActivity::class.java.simpleName
    private lateinit var ctx: Context
    private var queryAllAlgorithm = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUESTION_ALL_ALGORITHM
    private var queryAlgorithm = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUESTION_BY_SUB
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
        setContentView(R.layout.activity_article)
        HelpUtils.setStatusBar(window, R.color.colorPrimary)
        
        ctx = this
        initData()
    }

    private fun initData() {
        //初始化底部导航
        TabBarUtils().initStudyTabBar(ConstantUtils.INDEX_ARTICLE, this, ctx);

        //判断本地是否有内存
        //如果sp有数据
        sharedPreferences = getSharedPreferences("AlgorithmActivity", MODE_PRIVATE)
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

    private fun initView() {
        mainlist = findViewById<View>(R.id.al_mainlist) as ListView
        mainV2list = findViewById<View>(R.id.al_main2list) as ListView
        mainV3list = findViewById<View>(R.id.al_main3list) as ListView
        morelist = findViewById<View>(R.id.al_morelist) as ListView

        mainV3Adapter = CategoryV3MainAdapter(
            this@ArticleActivity,
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
            this@ArticleActivity,
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
            this@ArticleActivity,
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
        initV3Adapter(data[0].data)
        morelist!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            moreAdapter?.setSelectItem(position)
            moreAdapter?.notifyDataSetChanged()
            println("点击了第 " + headPosition + "," + firPosition + "," + secPosition + "," + position + "个位置")
            //# 跳转端
            val intent = Intent()
            intent.setClass(ctx, MarkdownActivity::class.java)
            intent.putExtra("content", questionData.get(position).articleContentMd)
            intent.putExtra("subCategoryId", data.get(headPosition).data.get(firPosition).data.get(secPosition).subCategoryId)
            intent.putExtra("subCategoryName", data.get(headPosition).data.get(firPosition).data.get(secPosition).subCategoryName)
            ctx.startActivity(intent)
        })

    }

    //HTTP GET
    fun queryByCategory() = GlobalScope.launch(Dispatchers.Main) {

        val progressDialog: ProgressDialog =
            ProgressDialog.show(ctx, "请稍等...", "大数据量，正获取数据中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) {
            http.httpGET2(
                queryAllAlgorithm,
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
                            queryAlgorithm + "?subCategoryId=" + subCategoryId,
                            200L, TimeUnit.MILLISECONDS
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

    private fun initV3Adapter(lists: List<QuestionV2ListEntity>) {

        mainV2Adapter = CategoryMainAdapter(
            this@ArticleActivity,
            lists
        )
        mainV2list?.setAdapter(mainV2Adapter)
        mainV2Adapter!!.notifyDataSetChanged()


        mainAdapter = SubCategoryMainAdapter(
            this@ArticleActivity,
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
            this@ArticleActivity,
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