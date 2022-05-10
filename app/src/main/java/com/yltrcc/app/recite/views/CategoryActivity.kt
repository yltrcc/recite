package com.yltrcc.app.recite.views

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.SharedPreferences
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.*
import com.yltrcc.app.recite.adapter.*
import com.yltrcc.app.recite.entity.*
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CategoryActivity : AppCompatActivity() {

    private lateinit var ctx: Context
    private var queryUrlAll =
        ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYALLV3_BY_CATEGORY_ID
    private lateinit var data: List<QuestionV3ListEntity>
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
        setContentView(R.layout.activity_category)

        ctx = this
        //判断本地是否有内存
        //如果sp有数据
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("CategoryActivity", MODE_PRIVATE)
        content = sharedPreferences.getString("content", "").toString();
        contentStr = sharedPreferences.getString("contentStr", "").toString();
        if (content.length > 0) {
            val gson = Gson()
            data = gson.fromJson(content, Array<QuestionV3ListEntity>::class.java).toList()
            initView()
        } else {
            queryByCateogry()
        }
    }

    //HTTP GET
    fun queryByCateogry() = GlobalScope.launch(Dispatchers.Main) {

        val progressDialog: ProgressDialog = ProgressDialog.show(ctx, "请稍等...", "大数据量，正获取数据中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET2(queryUrlAll, 30L) }.await()
            ?.let {
                val result = Gson().fromJson<Response<QuestionV3ListEntity>>(
                    it,
                    object : TypeToken<Response<QuestionV3ListEntity>>() {}.type
                )
                data = result.data
                val sharedPreferences: SharedPreferences =
                    getSharedPreferences("CategoryActivity", MODE_PRIVATE)
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

        mainlist = findViewById<View>(R.id.ca_mainlist) as ListView
        mainV2list = findViewById<View>(R.id.ca_main2list) as ListView
        mainV3list = findViewById<View>(R.id.ca_main3list) as ListView
        morelist = findViewById<View>(R.id.ca_morelist) as ListView

        mainV3Adapter = CategoryV3MainAdapter(
            this@CategoryActivity,
            data
        )
        mainV3Adapter!!.setSelectItem(0)
        mainV3list!!.setAdapter(mainV3Adapter)
        mainV3list!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            initV3Adapter(data.get(position).data)
            mainV3Adapter!!.setSelectItem(position)
            mainV3Adapter!!.notifyDataSetChanged()
            this.headPosition = position
            this.firPosition = 0
            this.secPosition = 0
        })
        mainV3list!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)



        mainV2Adapter = CategoryMainAdapter(
            this@CategoryActivity,
            data[0].data
        )
        mainV2Adapter!!.setSelectItem(0)
        mainV2list!!.setAdapter(mainV2Adapter)
        mainV2list!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            initV2Adapter(data.get(headPosition).data.get(position).data)
            mainV2Adapter!!.setSelectItem(position)
            mainV2Adapter!!.notifyDataSetChanged()
            this.firPosition = position
            this.secPosition = 0
        })
        mainV2list!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)

        mainAdapter = SubCategoryMainAdapter(
            this@CategoryActivity,
            data[0].data[0].data
        )
        mainAdapter!!.setSelectItem(0)
        mainlist!!.setAdapter(mainAdapter)
        mainlist!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            initAdapter(data.get(headPosition).data.get(firPosition).data.get(position).data)
            mainAdapter!!.setSelectItem(position)
            mainAdapter!!.notifyDataSetChanged()
            this.secPosition = position
        })
        mainlist!!.setChoiceMode(ListView.CHOICE_MODE_SINGLE)


        // 一定要设置这个属性，否则ListView不会刷新
        initV3Adapter(data[0].data)
        morelist!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            moreAdapter?.setSelectItem(position)
            moreAdapter?.notifyDataSetChanged()
            println("点击了第 " + headPosition + "," + firPosition + "," + secPosition + "," + position + "个位置")
            val entity: QuestionListEntity =
                data.get(headPosition).data.get(firPosition).data.get(secPosition)
            //Toast.makeText(ctx, "Clicked item :" + " " + position + " id: " + id + " name: " + entity.articleTitle, Toast.LENGTH_SHORT).show()
            //# 跳转端
            val intent = Intent()
            intent.setClass(ctx, MarkdownActivity::class.java)
            intent.putExtra("content", entity.data.get(position).articleContentMd)
            ctx.startActivity(intent)
        })

    }

    private fun initV3Adapter(lists: List<QuestionV2ListEntity>) {

        mainV2Adapter = CategoryMainAdapter(
            this@CategoryActivity,
            lists
        )
        mainV2list?.setAdapter(mainV2Adapter)
        mainV2Adapter!!.notifyDataSetChanged()


        mainAdapter = SubCategoryMainAdapter(
            this@CategoryActivity,
            lists[0].data
        )
        mainlist?.setAdapter(mainAdapter)
        mainAdapter!!.notifyDataSetChanged()

        moreAdapter =
            SubCategoryMoreAdapter(this, lists[0].data[0].data)
        morelist?.setAdapter(moreAdapter)
        moreAdapter!!.notifyDataSetChanged()

        val btnHomepage: Button = findViewById(R.id.category_btn_homepage)
        val btnAlgorithm: Button = findViewById(R.id.category_btn_algorithm)
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
    }


    private fun initV2Adapter(lists: List<QuestionListEntity>) {

        mainAdapter = SubCategoryMainAdapter(
            this@CategoryActivity,
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

    @Override
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}