package com.yltrcc.app.recite

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yltrcc.app.recite.utils.ConstantUtils
import com.yltrcc.app.recite.utils.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class QuestionDetailsActivity : AppCompatActivity() {


    private lateinit var webView: WebView

    private var randomId: Int = -1
    private var count: Int = 2
    private var PAGE_URL = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_PAGE;
    private var PAGE_COUNT = ConstantUtils.BASE_API + ConstantUtils.QUESTION_GET_COUNT;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details)

        val nextOne: Button = findViewById(R.id.bun_next_one)
        nextOne.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {

                onParallelGetButtonClick()
            }
        })
        initWebView()

        val ReturnHome: Button = findViewById(R.id.bun_return_home)
        ReturnHome.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                finish()
            }
        })
    }

    //HTTP GET
    fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        //不能在UI线程进行请求，使用async起到后台线程，使用await获取结果
        async(Dispatchers.Default) { http.httpGET1(PAGE_URL + "?id=" + randomId) }.await()
            ?.let {
                //判断是否超出最大值
                if (randomId + 1 >= count) {
                    randomId = Random().nextInt(count) + 1
                }else {
                    randomId = randomId + 1
                }
                webView?.loadUrl(PAGE_URL + "?id=" + randomId)
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

    fun initWebView() {
        //获取总的题库数
        getCount()
        //初始化 randomId 值
        val random = Random()
        randomId = random.nextInt(count) + 1
        webView = findViewById(R.id.wb_content)
        webView?.loadUrl(PAGE_URL + "?id=" + randomId)

        val webClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }

        //下面这些直接复制就好
        webView?.webViewClient = webClient

        var webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true  // 开启 JavaScript 交互
        webSettings.setAppCacheEnabled(true) // 启用或禁用缓存
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // 只要缓存可用就加载缓存, 哪怕已经过期失效 如果缓存不可用就从网络上加载数据
        webSettings.setAppCachePath(cacheDir.path) // 设置应用缓存路径

        // 缩放操作
        webSettings.setSupportZoom(true) // 支持缩放 默认为true 是下面那个的前提
        webSettings.builtInZoomControls = true // 设置内置的缩放控件 若为false 则该WebView不可缩放
        webSettings.displayZoomControls = false // 隐藏原生的缩放控件

        webSettings.blockNetworkImage = false // 禁止或允许WebView从网络上加载图片
        webSettings.loadsImagesAutomatically = true // 支持自动加载图片

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.safeBrowsingEnabled = true // 是否开启安全模式
        }

        webSettings.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
        webSettings.domStorageEnabled = true // 启用或禁用DOM缓存
        webSettings.setSupportMultipleWindows(true) // 设置WebView是否支持多窗口

        // 设置自适应屏幕, 两者合用
        webSettings.useWideViewPort = true  // 将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true  // 缩放至屏幕的大小
        webSettings.allowFileAccess = true // 设置可以访问文件

        webSettings.setGeolocationEnabled(true) // 是否使用地理位置

        webView?.fitsSystemWindows = true
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView?.loadUrl(PAGE_URL + "?id=" + randomId)

        //设置字体大小
        webSettings.setSupportZoom(true);
        webSettings.setTextZoom(300)
    }

    //设置返回键的监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val webView: WebView = findViewById(R.id.wb_content)
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView!!.canGoBack()) {
                webView!!.goBack()  //返回上一个页面
                return true
            } else {
                finish()
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        //释放资源
        webView.destroy()
    }

}