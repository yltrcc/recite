package com.yltrcc.app.recite.activity.study

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
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HelpUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.*
import okhttp3.FormBody
import java.util.concurrent.TimeUnit

class EditActivity : AppCompatActivity() {

    private lateinit var ctx: Context
    private var UPDATE_API = ConstantUtils.BASE_API + ConstantUtils.UPDATE_CONTENT
    private var id: Long = -1
    private var isSuccess: Int = -1
    private var content: String = ""
    private lateinit var etContent: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HelpUtils.setStatusBar(window, R.color.colorPrimary)
        setContentView(R.layout.activity_edit)
        val saveBtn: Button = findViewById(R.id.edit_btn_save)
        val cancelBtn: Button = findViewById(R.id.edit_btn_canel)
        etContent = findViewById(R.id.edit_et_content)
        ctx = this
        //接收内容
        content = intent.getStringExtra("content").toString()
        etContent.text = content
        id = intent.getLongExtra("id", -1)

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
    fun saveContent(id: Long) =
        GlobalScope.launch(Dispatchers.Main) {

            val progressDialog: ProgressDialog =
                ProgressDialog.show(ctx, "请稍等...", "更新数据中...", true)
            isSuccess = -1;
            val http = HttpUtil()
            supervisorScope {
                try {
                    //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
                    async(Dispatchers.Default) {
                        val formBody: FormBody = FormBody.Builder()
                            .add("id", id.toString())
                            .add("content", etContent.text.toString())
                            .build()
                        http.post(UPDATE_API, 200L, TimeUnit.MILLISECONDS, formBody)
                    }.await()?.let {
                        //2秒后跳转
                        Thread.sleep(1 * 1000)
                        progressDialog.dismiss();//去掉加载框
//                        Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
                        //跳转到编辑详情页面
                        val intent = Intent()
                        intent.setClass(ctx, QuestionActivity::class.java)
                        ctx.startActivity(intent)
                        finish()
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.toString())
                    Toast.makeText(ctx, "嗷呜，出错了，请联系管理员！", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss();//去掉加载框
                }
            }
        }

}