package com.yltrcc.app.recite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.content.Intent
import android.widget.LinearLayout
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var PAGE_COUNT = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_COUNT
    private var count: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCount()

        val javaBase: LinearLayout = findViewById(R.id.ll_java_base)
        javaBase.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, QuestionTestActivity::class.java)
            startActivity(intent)
        }

        val javaRandom: LinearLayout = findViewById(R.id.ll_java_random)
        javaRandom.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, QuestionDetailsActivity::class.java)
            intent.putExtra("count", count)
            startActivity(intent)
        }
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
}