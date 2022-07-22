package com.yltrcc.app.recite.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.utils.StatusBarUtils
import com.yltrcc.app.recite.views.MarkdownWebView

class MarkdownActivity : AppCompatActivity() {

    private lateinit var ctx: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBar(window, R.color.colorPrimary)
        setContentView(R.layout.activity_markdown)
        val markdownWebView: MarkdownWebView = findViewById(R.id.markdown_view)
        val cvBtn: Button = findViewById(R.id.mk_cv)
        ctx = this
        //接收内容
        val content = intent.getStringExtra("content")
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

        markdownWebView.setText(content)
    }
}