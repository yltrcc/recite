package com.yltrcc.app.recite.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.entity.QuestionV3ListEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.*


class SplashActivity : AppCompatActivity() {

    private val TAG = SplashActivity::class.java.simpleName

    private var queryUrlAll =
        ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUERYALLV3
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ctx = this
        // 修改状态栏字体颜色，用AndroidX官方兼容API
        val wic: WindowInsetsControllerCompat? =
            ViewCompat.getWindowInsetsController(getWindow().getDecorView())
        if (wic != null) {
            // true表示Light Mode，状态栏字体呈黑色，反之呈白色
            wic.setAppearanceLightStatusBars(false);
        }

        // 修改状态栏背景颜色，还是通用API，这个比较简单
        getWindow().setStatusBarColor(Color.BLACK)
        val detailsImage: ImageView = findViewById(R.id.splash_img)

        //异步拉取数据
        jobAll()
        skip()
    }

    private fun jobAll() {
        job1()
        //job2()
    }

    fun skip() {

        val t = Thread { //这个函数接口里，编写新线程需要干的事情
            // TODO Auto-generated method stub
            try {
                Thread.sleep(1500) //睡眠1.5s
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, MainActivity::class.java)
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
     * APP版本更新判断
     * https://gitee.com/api/v5/repos/yltrcc/recite/contents/%2Fapk%2F1.txt
     * 请求最新版本号保存到本地sp
     */
    fun job2() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()

        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET2(ConstantUtils.UPDATE_VERIFY_URL, 30L) }.await()
            ?.let {
                //表示数据不一致 需要更新
                val sharedPreferences: SharedPreferences =
                    getSharedPreferences("versionCode", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("data",it.toInt())
                editor.apply()
            }
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
                async(Dispatchers.Default) { http.httpGET2(queryUrlAll, 30L) }.await()
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
}