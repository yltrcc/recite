package com.yltrcc.app.recite.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.activity.study.StudyHomePageActivity
import com.yltrcc.app.recite.entity.QuestionV3ListEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HelpUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class SplashActivity : AppCompatActivity() {

    private val TAG = SplashActivity::class.java.simpleName

    private var queryUrlAll = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYALLV3
    private var queryAllAlgorithm = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUESTION_ALL_ALGORITHM
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ctx = this
        HelpUtils.setStatusBar(window, R.color.black)

        //异步拉取数据
        jobAll()
        skip()
    }

    private fun jobAll() {
        job1()
        job2()
    }

    fun skip() {

        val t = Thread { //这个函数接口里，编写新线程需要干的事情
            // TODO Auto-generated method stub
            try {
                Thread.sleep(1500) //睡眠1.5s
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, StudyHomePageActivity::class.java)
                ctx.startActivity(intent)
                finish()
            } catch (e: InterruptedException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        } //实例化一个线程

        t.start() //启动线程


    }



    /**
     * 异步去请求请求数据然后更新到 sp
     */
    //异步起一个线程去更新content
    fun job1() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        supervisorScope {
            try {
                //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
                async(Dispatchers.Default) { http.httpGET2(queryUrlAll, 4L, TimeUnit.SECONDS) }.await()
                    ?.let {
                        val result = Gson().fromJson<Response<QuestionV3ListEntity>>(
                            it,
                            object : TypeToken<Response<QuestionV3ListEntity>>() {}.type
                        )
                        //
                        val sharedPreferences: SharedPreferences =
                            getSharedPreferences("CategoryActivity", MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("content", Gson().toJson(result.data))
                        editor.putString("contentStr", it)
                        editor.apply()
                    }
            }catch (ex:Exception) {
                Log.e(TAG, ex.toString())
            }
        }


    }

    /**
     * 异步去请求请求数据然后更新到 sp
     */
    //异步起一个线程去更新content
    fun job2() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        supervisorScope {
            try {
                //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
                async(Dispatchers.Default) { http.httpGET2(queryAllAlgorithm, 1L, TimeUnit.SECONDS) }.await()
                    ?.let {
                        val result = Gson().fromJson<Response<QuestionV3ListEntity>>(
                            it,
                            object : TypeToken<Response<QuestionV3ListEntity>>() {}.type
                        )
                        //
                        val sharedPreferences: SharedPreferences =
                            getSharedPreferences("AlgorithmActivity", MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("content", Gson().toJson(result.data))
                        editor.putString("contentStr", it)
                        editor.apply()
                    }
            }catch (ex:Exception) {
                Log.e(TAG, ex.toString())
            }
        }


    }
}