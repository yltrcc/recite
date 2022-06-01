package com.yltrcc.app.recite.views

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.utils.StatusBarUtils

class MarkdownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBar(window, R.color.colorPrimary)
        setContentView(R.layout.activity_markdown)
        val markdownWebView: MarkdownWebView = findViewById(R.id.markdown_view)

        //接收内容
        val content = intent.getStringExtra("content")

        markdownWebView.setText(content)
    }
}