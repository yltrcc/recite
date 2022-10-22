package com.yltrcc.app.recite.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.entity.QuestionEntity
import com.yltrcc.app.recite.entity.QuestionListEntity
import com.yltrcc.app.recite.entity.QuestionV2ListEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import com.yltrcc.app.recite.utils.StatusBarUtils
import com.yltrcc.app.recite.views.MarkdownWebView
import com.youbenzi.mdtool.tool.MDTool
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class EditActivity : AppCompatActivity() {

    private lateinit var ctx: Context
    private var queryData = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUESTION_BY_SUB
    private var id: Int = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBar(window, R.color.colorPrimary)
        setContentView(R.layout.activity_edit)
        val saveBtn: Button = findViewById(R.id.edit_btn_save)
        val cancelBtn: Button = findViewById(R.id.edit_btn_canel)
        val etContent: TextView = findViewById(R.id.edit_et_content)
        ctx = this
        //接收内容
        val content = intent.getStringExtra("content")
        etContent.text = content
        id = intent.getIntExtra("id", -1)

        cancelBtn.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                finish()
            }
        })
        saveBtn.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                saveContent(id)
            }
        })
    }

    //HTTP GET
    /**
     * 根据id保存内容
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun saveContent(id: Int) =
        GlobalScope.launch(Dispatchers.Main) {

            val progressDialog: ProgressDialog =
                ProgressDialog.show(ctx, "请稍等...", "保存数据中...", true)

            val http = HttpUtil()
            supervisorScope {
                try {
                    //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
                    async(Dispatchers.Default) {
                        http.httpGET2(
                            queryData + "?id=" + id,
                            200L, TimeUnit.MILLISECONDS
                        )
                    }.await()
                        ?.let {
                            val result = Gson().fromJson<Response<QuestionListEntity>>(
                                it,
                                object : TypeToken<Response<QuestionListEntity>>() {}.type
                            )

                            Toast.makeText(ctx, "已复制到粘贴板中！", Toast.LENGTH_SHORT).show()
                        }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.toString())

                    Toast.makeText(ctx, "嗷呜，出错了，请联系管理员！", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss();//去掉加载框
                }
            }

        }

}