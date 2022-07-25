package com.yltrcc.app.recite.activity

import android.app.ProgressDialog
import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.entity.QuestionEntity
import com.yltrcc.app.recite.entity.QuestionListEntity
import com.yltrcc.app.recite.entity.Response
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import com.yltrcc.app.recite.utils.StatusBarUtils
import com.yltrcc.app.recite.views.MarkdownWebView
import com.youbenzi.mdtool.tool.MDTool
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MarkdownActivity : AppCompatActivity() {

    private lateinit var ctx: Context
    private var queryArticle = ConstantUtils.BASE_API + ConstantUtils.QUESTION_QUESTION_BY_SUB
    private lateinit var questionData: MutableList<QuestionEntity>
    private var allContent:String = ""
    private var subCategoryName:String = ""
    private var subCategoryId:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBar(window, R.color.colorPrimary)
        setContentView(R.layout.activity_markdown)
        val markdownWebView: MarkdownWebView = findViewById(R.id.markdown_view)
        val cvBtn: Button = findViewById(R.id.mk_cv)
        val cvCategoryBtn: Button = findViewById(R.id.mk_category_cv)
        ctx = this
        //接收内容
        val content = intent.getStringExtra("content")
        subCategoryId = intent.getIntExtra("subCategoryId", -1)
        subCategoryName = intent.getStringExtra("subCategoryName").toString()
        if (-1 != subCategoryId) {
            cvCategoryBtn.visibility = View.VISIBLE
        }else {
            cvCategoryBtn.visibility = View.GONE
        }

        cvBtn.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面

                // Gets a handle to the clipboard service.
                val clipboard:ClipboardManager  = getSystemService (Context.CLIPBOARD_SERVICE) as ClipboardManager

                // Creates a new text clip to put on the clipboard
                val clip:ClipData  = ClipData.newPlainText ("simple text", content);

                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ctx, "已复制到粘贴板中！", Toast.LENGTH_SHORT).show()
            }
        })
        cvCategoryBtn.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                queryBySubCategory()
            }
        })
        markdownWebView.setText(content)
    }

    //HTTP GET
    /**
     * 通过子分类 拉取 面试题
     */
    fun queryBySubCategory() =
        GlobalScope.launch(Dispatchers.Main) {

            val progressDialog: ProgressDialog =
                ProgressDialog.show(ctx, "请稍等...", "获取数据中...", true)

            val http = HttpUtil()
            supervisorScope {
                try {
                    //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
                    async(Dispatchers.Default) {
                        http.httpGET2(
                            queryArticle + "?subCategoryId=" + subCategoryId,
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
                                allContent = ""
                                allContent += "# " + subCategoryName + "\n\n"
                                for ( entity:QuestionEntity in questionData) {
                                    allContent += entity.articleContentMd + "\n\n"
                                }
                            } else {
                                Toast.makeText(ctx, "暂无数据", Toast.LENGTH_SHORT).show()
                            }
                            Thread.sleep(1000)
                            progressDialog.dismiss();//去掉加载框
                            // Gets a handle to the clipboard service.
                            val clipboard:ClipboardManager  = getSystemService (Context.CLIPBOARD_SERVICE) as ClipboardManager

                            // Creates a new text clip to put on the clipboard
                            val clip:ClipData  = ClipData.newPlainText ("simple text", allContent);

                            // Set the clipboard's primary clip.
                            clipboard.setPrimaryClip(clip);

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