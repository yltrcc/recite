package com.yltrcc.app.recite.views

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.entity.QuestionCategoryEntity
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.DownloadUtil
import java.io.File


class MainActivity : AppCompatActivity() {

    private var PAGE_COUNT = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_COUNT
    private var CATEGORIES = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_CATEGORY
    private var count: Int = 2
    private lateinit var data: MutableList<QuestionCategoryEntity>
    private lateinit var ctx: Context
    private lateinit var file:File
    val downloadUrl = "https://gitee.com/api/v5/repos/yltrcc/recite/contents/apk/app-debug.apk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ctx = this
        init() //初始化列表数据

        //如果sp有数据
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("questionDetails", MODE_PRIVATE)

        val content: String? = sharedPreferences.getString("content", null)
        val title: String? = sharedPreferences.getString("title", null)


        val clickHistory: TextView = findViewById(R.id.main_tv_click_history)
        val btnCategory: Button = findViewById(R.id.main_btn_category)
        val btnAlgorithm: Button = findViewById(R.id.main_btn_algorithm)
        if (content != null && title != null) {
            val text: String
            if (title.length <= 17) {
                text = "最近浏览：" + title
            } else {
                text = "最近浏览：" + (title?.slice(0..17) ?: String) + "..."
            }

            clickHistory.setText(text)
        }
        clickHistory.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面
                val intent = Intent()
                intent.setClass(ctx, QuestionDetailsActivity::class.java)
                intent.putExtra("content", content)
                intent.putExtra("title", title)
                ctx.startActivity(intent)
            }
        })
        btnCategory.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, CategoryActivity::class.java)
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

        //测试
        val ceshi: Button = findViewById(R.id.main_btn_ceshi)
        ceshi.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, SubV2CategoryActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })
        //测试
        val ceshi2: Button = findViewById(R.id.main_btn_ceshi2)
        ceshi2.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, SubV3CategoryActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })
        //测试
        val ceshi3: Button = findViewById(R.id.main_btn_ceshi3)
        ceshi3.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, MarkdownActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })
        //测试
        val ceshi4: Button = findViewById(R.id.main_btn_ceshi4)
        ceshi4.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                overridePendingTransition(0, 0)
                intent.setClass(ctx, SplashActivity::class.java)
                ctx.startActivity(intent)
                finish()
            }
        })
    }

    private fun init() {
        //判断是否需要更新APP https://gitee.com/yltrcc/recite/raw/master/apk/app-debug.apk
        //获取apk的版本号 currentVersionCode
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("versionCode", MODE_PRIVATE)
        val versionCode: Int = sharedPreferences.getInt("data", -1)
        val packageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        val localVersionCode = packageInfo.longVersionCode
        if (versionCode != -1 && versionCode > localVersionCode) {
            //表示此时需要更新APP
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("升级提示").setMessage("检测到有新版本，请升级")
                .setPositiveButton("后台升级",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        //后台请求保存更新文件
                        doDownload()
                    }).show()
        }

    }

    @Override
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    //下载升级包
    private fun doDownload() {

        var parentPath = ""
        try {
            parentPath = getExternalFilesDir(null)!!.path
        } catch (e: Exception) {
            Log.d(TAG, "doDownload e:" + e.message)
        }
        Log.d(TAG, "doDownload parentPath:$parentPath")
        file = File(parentPath, "my.apk")
        val filePath: String = file.getAbsolutePath()

        //如果已有文件，删除
        if (file.exists()) {
            Log.d(TAG, "doDownload delete APK")
            file.delete()
        }
        try {
            DownloadUtil.get().download(downloadUrl, filePath, object : DownloadUtil.OnDownloadListener{
                override fun onDownloadSuccess() {
                    //成功
                    Log.d(TAG, "doDownload download success");
                    installApk();
                }

                override fun onDownloading(progress: Int) {
                    //进度
                    //Log.d(TAG, "doDownload download:" + progress +"%");
                }

                override fun onDownloadFailed() {
                    //失败
                    Log.d(TAG, "doDownload download fail");
                }

            })
        } catch (e: Exception) {
            Log.d(TAG, "doDownload e2:" + e.message)
        }
    }

    //安装app
    private fun installApk() {
        val intent = Intent(Intent.ACTION_VIEW)
        val data: Uri

        //7.0以上安卓系统安装app需要通过fileProvider（需要在AndroidManifest.xml声明）
        //注意包名要和 AndroidManifest.xml 中的 android:authorities="com.yltrcc.app.recite.fileProvider" 一致
        data = FileProvider.getUriForFile(this, "com.yltrcc.app.recite.fileProvider", file)
        // 给目标应用一个临时授权
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //安装完成后打开新版本
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Log.d(TAG, "installApk 7.0data:$data")
        intent.setDataAndType(data, "application/vnd.android.package-archive")
        this.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

}