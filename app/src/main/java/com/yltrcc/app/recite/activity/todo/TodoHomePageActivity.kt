package com.yltrcc.app.recite.activity.todo

import android.app.Notification
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.activity.study.ChatActivity
import com.yltrcc.app.recite.activity.study.MarkdownActivity
import com.yltrcc.app.recite.activity.study.QuestionActivity
import com.yltrcc.app.recite.adapter.ContactAdapter
import com.yltrcc.app.recite.adapter.SimpleMenuAdapter
import com.yltrcc.app.recite.bean.ContactShowInfo
import com.yltrcc.app.recite.component.PopupMenuWindows
import com.yltrcc.app.recite.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


class TodoHomePageActivity : AppCompatActivity() {

    private lateinit var ctx: Context
    private lateinit var file: File

    val TYPE_USER = 0x11
    val TYPE_SERVICE = 0X12
    val TYPE_SUBSCRIBE = 0x13
    private var toolbarHeight = 0
    private var statusBarHeight: Int = 0

    private val mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage_todo)
        HelpUtils.setStatusBar(window, R.color.colorPrimary)

        ctx = this

        //初始化数据
        initData()
    }

    private fun initData() {

        //初始化 头部导航栏
        initToolBar()

        //初始化 底部导航栏
        TabBarUtils().initTodoTabBar(ConstantUtils.INDEX_STUDY_HOME, this, ctx);

        //更新 app 逻辑
        updateApp();

        //初始化 学习进度
        initStudyProgress();

        //初始化 首页微信数据
        initChatData()

        val randomArticle: Button = findViewById(R.id.main_random_article)
        randomArticle.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                getRandomArticle()
            }
        })
    }

    private fun initToolBar() {

        val bar = findViewById<Toolbar>(R.id.activity_wechat_toolbar)
        setSupportActionBar(bar)
        supportActionBar!!.setTitle("")


        bar.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        toolbarHeight = bar.measuredHeight
        statusBarHeight = HelpUtils.getStatusBarHeight(this@TodoHomePageActivity)
    }


    fun sendChatMsg(view: View?) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = NotificationCompat.Builder(this, "chat")
            .setContentTitle("收到一条聊天消息")
            .setContentText("今天中午吃什么？")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.diannao)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.diannao))
            .setAutoCancel(true)
            .build()
        manager.notify(1, notification)
    }

    fun sendSubscribeMsg(view: View?) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = NotificationCompat.Builder(this, "subscribe")
            .setContentTitle("收到一条订阅消息")
            .setContentText("地铁沿线30万商铺抢购中！")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.diannao)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.diannao))
            .setAutoCancel(true)
            .build()
        manager.notify(2, notification)
    }



    private fun initStudyProgress() {

        //如果sp有数据
        val taskCategorySP: SharedPreferences = getSharedPreferences("taskCategory", MODE_PRIVATE)

        val questionValue: String? = taskCategorySP.getString("question", "")
        val algorithmValue: String? = taskCategorySP.getString("algorithm", "")


        val taskCategory: TextView = findViewById(R.id.main_category)
        val startLearn: Button = findViewById(R.id.main_start_learn)

        var text: String = ""
        if ((questionValue != null) && questionValue.isNotEmpty()) {
            text += "" + questionValue.split("@")[1] + '\n'
        }
        if ((algorithmValue != null) && algorithmValue.isNotEmpty()) {
            text += "" + algorithmValue.split("@")[1] + '\n'
        }
        if (text.length == 0) {
            text = "java核心知识"
        }
        taskCategory.setText(text)

        startLearn.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题分类 ~@~!
                val intent = Intent()
                intent.setClass(ctx, QuestionActivity::class.java)
                if (questionValue == "") {
                    intent.putExtra("index", "0")
                } else {
                    intent.putExtra("index", questionValue!!.split("@")[0])
                }
                ctx.startActivity(intent)
            }
        })

    }

    private fun updateApp() {
        //判断是否需要更新APP https://gitee.com/yltrcc/recite/raw/master/apk/app-debug.apk
        //获取apk的版本号 currentVersionCode
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("versionCode", MODE_PRIVATE)
        val versionCode: Int = sharedPreferences.getInt("data", -1)
        val packageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        val localVersionCode = packageInfo.longVersionCode
        /*if (versionCode != -1 && versionCode > localVersionCode) {
            //表示此时需要更新APP
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("升级提示").setMessage("检测到有新版本，请升级")
                .setPositiveButton("后台升级",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        //后台请求保存更新文件
                        doDownload()
                    }).show()
        }*/
    }



    //HTTP GET
    fun getRandomArticle() = GlobalScope.launch(Dispatchers.Main) {

        val progressDialog: ProgressDialog =
            ProgressDialog.show(ctx, "请稍等...", "正生成内容中...", true)

        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) {
            http.httpGET2(
                ConstantUtils.RANDOM_ARTICLE,
                9L, TimeUnit.SECONDS
            )
        }.await()
            ?.let {

                progressDialog.dismiss();//去掉加载框 val intent = Intent()
                val intent = Intent()
                intent.setClass(ctx, MarkdownActivity::class.java)
                intent.putExtra("content", it)
                ctx.startActivity(intent)
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
            DownloadUtil.get().download(
                ConstantUtils.UPDATE_URL,
                filePath,
                object : DownloadUtil.OnDownloadListener {
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
    private fun initChatData() {
        val lv = findViewById<ListView>(R.id.activity_wechat_lv)
        val headImgRes = intArrayOf(
            R.drawable.hdimg_3, R.drawable.group1, R.drawable.hdimg_2, R.drawable.user_2,
            R.drawable.user_3, R.drawable.user_4, R.drawable.user_5, R.drawable.hdimg_4,
            R.drawable.hdimg_5, R.drawable.hdimg_6
        )
        val usernames = arrayOf(
            "Fiona", "  ...   ", "冯小", "深圳社保", "服务通知", "招商银行信用卡",
            "箫景、Fiona", "吴晓晓", "肖箫", "唐小晓"
        )
        //最新消息
        val lastMsgs = arrayOf(
            "我看看", "吴晓晓：无人超市啊", "最近在忙些什么", "八月一号猛料，内地社保在这2...",
            "微信支付凭证", "#今日签到#你能到大的，比想象...", "箫景:准备去哪嗨", "[Video Call]", "什么东西？", "[微信红包]"
        )
        val lastMsgTimes = arrayOf(
            "17:40", "10:56", "7月26日", "昨天", "7月27日", "09:46",
            "7月18日", "星期一", "7月26日", "4月23日"
        )
        val types = intArrayOf(
            this.TYPE_USER,
            this.TYPE_USER,
            this.TYPE_USER,
            this.TYPE_SUBSCRIBE,
            this.TYPE_SERVICE,
            this.TYPE_SUBSCRIBE,
            this.TYPE_USER,
            this.TYPE_USER,
            this.TYPE_USER,
            this.TYPE_USER
        )
        //静音&已读
        val isMutes =
            booleanArrayOf(false, true, false, false, false, false, true, false, false, false)
        val isReads = booleanArrayOf(true, true, true, true, true, true, true, true, true, true)
        val infos: MutableList<ContactShowInfo> = mutableListOf<ContactShowInfo>()
        for (i in headImgRes.indices) {
            infos.add(
                i, ContactShowInfo(
                    headImgRes[i],
                    usernames[i], lastMsgs[i], lastMsgTimes[i], isMutes[i], isReads[i], types[i]
                )
            )
        }
        val adapter = ContactAdapter(this, R.layout.item_wechat_main, infos)
        lv.adapter = adapter
        lv.setOnTouchListener(object : OnTouchListener {
            var preX = 0
            var preY = 0
            var isSlip = false
            var isLongClick = false
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        preX = event.x.toInt()
                        preY = event.y.toInt()
                        mHandler.postDelayed({
                            isLongClick = true
                            val x = event.x.toInt()
                            val y = event.y.toInt()
                            //延时500ms后，其Y的坐标加入了Toolbar和statusBar高度
                            val position =
                                lv.pointToPosition(x, y - toolbarHeight - statusBarHeight)
                            initPopupMenu(v, x, y, adapter, position, infos)
                        }, 500)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val nowX = event.x.toInt()
                        val nowY = event.y.toInt()
                        val movedX = Math.abs(nowX - preX)
                        val movedY = Math.abs(nowY - preY)
                        if (movedX > 50 || movedY > 50) {
                            isSlip = true
                            mHandler.removeCallbacksAndMessages(null)
                            //处理滑动事件
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        mHandler.removeCallbacksAndMessages(null)
                        if (!isSlip && !isLongClick) {
                            //处理单击事件
                            val position = lv.pointToPosition(preX, preY)
                            val intent = Intent(this@TodoHomePageActivity, ChatActivity::class.java)
                            intent.putExtra("name", usernames[position])
                            intent.putExtra("profileId", headImgRes[position])
                            startActivity(intent)
                        } else {
                            isSlip = false
                            isLongClick = false
                        }
                    }
                }
                return false
            }
        })
    }

    /**
     * 设置已读还是未读
     *
     * @param isRead   true已读，false未读
     * @param position item position
     * @param adapter  数据源
     * @param datas
     */
    private fun setIsRead(
        isRead: Boolean,
        position: Int,
        adapter: ContactAdapter,
        datas: List<ContactShowInfo>
    ) {
        val info = datas[position]
        info.isRead = isRead
        adapter.notifyDataSetChanged()
    }

    /**
     * 删除指定位置item
     *
     * @param position 指定删除position
     * @param adapter  数据源
     * @param datas
     */
    private fun deleteMsg(position: Int, adapter: ContactAdapter, datas: MutableList<ContactShowInfo>) {
        datas.removeAt(position)
        adapter.notifyDataSetChanged()
    }

    /**
     * 初始化popup菜单
     */
    private fun initPopupMenu(
        anchorView: View,
        posX: Int,
        posY: Int,
        adapter: ContactAdapter,
        itemPos: Int,
        data: MutableList<ContactShowInfo>
    ) {
        val list: MutableList<String?> = ArrayList()
        val showInfo = data[itemPos]
        when (showInfo.accountType) {
            this.TYPE_SERVICE -> {
                list.clear()
                if (showInfo.isRead) {
                    list.add("标为未读")
                } else {
                    list.add("标为已读")
                }
                list.add("删除该聊天")
            }
            this.TYPE_SUBSCRIBE -> {
                if (showInfo.isRead) {
                    list.add("标为未读")
                } else {
                    list.add("标为已读")
                }
                list.add("置顶公众号")
                list.add("取消关注")
                list.add("删除该聊天")
            }
            this.TYPE_USER -> {
                list.clear()
                if (showInfo.isRead) {
                    list.add("标为未读")
                } else {
                    list.add("标为已读")
                }
                list.add("置顶聊天")
                list.add("删除该聊天")
            }
        }
        val menuAdapter = SimpleMenuAdapter<String>(this, R.layout.item_menu, list)
        val ppm = PopupMenuWindows(this, R.layout.popup_menu_general_layout, menuAdapter)
        val posArr = ppm.reckonPopWindowShowPos(posX, posY)
        ppm.setAutoFitStyle(true)
        ppm.setOnMenuItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            when (list[position]) {
                "标为未读" -> setIsRead(false, itemPos, adapter, data)
                "标为已读" -> setIsRead(true, itemPos, adapter, data)
                "置顶聊天", "置顶公众号" -> stickyTop(adapter, data, itemPos)
                "取消关注", "删除该聊天" -> deleteMsg(itemPos, adapter, data)
            }
            ppm.dismiss()
        }
        ppm.showAtLocation(anchorView, Gravity.NO_GRAVITY, posArr[0], posArr[1])
    }


    /**
     * 置顶item
     *
     * @param adapter
     * @param datas
     */
    private fun stickyTop(adapter: ContactAdapter, datas: MutableList<ContactShowInfo>, position: Int) {
        if (position > 0) {
            val stickyTopItem = datas[position]
            datas.removeAt(position)
            datas.add(0, stickyTopItem)
        } else {
            return
        }
        adapter.notifyDataSetChanged()
    }
}