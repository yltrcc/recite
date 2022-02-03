package com.yltrcc.app.recite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.content.Intent
import android.widget.LinearLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val javaBase: LinearLayout = findViewById(R.id.ll_java_base)
        javaBase.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, QuestionTestActivity::class.java)
            startActivity(intent)
        }

        val javaRandom: LinearLayout = findViewById(R.id.ll_java_random)
        javaRandom.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, QuestionDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}