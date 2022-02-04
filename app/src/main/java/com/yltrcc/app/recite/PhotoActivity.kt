package com.yltrcc.app.recite

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import coil.load

class PhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        //接收 intent 数据
        val imageUrl = intent.getStringExtra("image_url")
        val detailsImage: ImageView = findViewById(R.id.image_details)
        // URL
        detailsImage.load(imageUrl)
    }
}