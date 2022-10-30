package com.yltrcc.app.recite.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.activity.novel.NovelHomePageActivity
import com.yltrcc.app.recite.activity.study.*
import com.yltrcc.app.recite.activity.todo.TodoHomePageActivity


class TabBarUtils {

    fun initStudyTabBar(index: Int, cusThis: Activity, ctx: Context) {
        val btnCategory: Button = cusThis.findViewById(R.id.block_study_btn_category)
        val btnAlgorithm: Button = cusThis.findViewById(R.id.block_study_btn_algorithm)
        val btnArticle: Button = cusThis.findViewById(R.id.block_study_btn_article)
        val btnHomePage: Button = cusThis.findViewById(R.id.block_study_btn_homepage)
        btnHomePage.setTextColor(ctx.getColor(R.color.black))
        btnCategory.setTextColor(ctx.getColor(R.color.black))
        btnAlgorithm.setTextColor(ctx.getColor(R.color.black))
        btnArticle.setTextColor(ctx.getColor(R.color.black))
        when (index) {
            ConstantUtils.INDEX_STUDY_HOME -> btnHomePage.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_QUESTION -> btnCategory.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_ALGORITHM -> btnAlgorithm.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_ARTICLE -> btnArticle.setTextColor(ctx.getColor(R.color.red))
        }
        btnHomePage.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面
                val intent = Intent()
                cusThis.overridePendingTransition(0, 0)
                intent.setClass(ctx, StudyHomePageActivity::class.java)
                ctx.startActivity(intent)
                cusThis.finish()
            }
        })
        //长按 选择 另一个 大模块
        btnHomePage.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                initModules(cusThis, ctx)
                return true
            }

        })
        btnCategory.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的面试题详情页面
                val intent = Intent()
                cusThis.overridePendingTransition(0, 0)
                intent.setClass(ctx, QuestionActivity::class.java)
                ctx.startActivity(intent)
                cusThis.finish()
            }
        })
        btnAlgorithm.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                cusThis.overridePendingTransition(0, 0)
                intent.setClass(ctx, AlgorithmActivity::class.java)
                ctx.startActivity(intent)
                cusThis.finish()
            }
        })
        btnArticle.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                //跳转到具体的算法分类页面
                val intent = Intent()
                cusThis.overridePendingTransition(0, 0)
                intent.setClass(ctx, ArticleActivity::class.java)
                ctx.startActivity(intent)
                cusThis.finish()
            }
        })
    }

    fun initTodoTabBar(index: Int, cusThis: Activity, ctx: Context) {
        val btnCategory: Button = cusThis.findViewById(R.id.block_todo_btn_category)
        val btnAlgorithm: Button = cusThis.findViewById(R.id.block_todo_btn_algorithm)
        val btnArticle: Button = cusThis.findViewById(R.id.block_todo_btn_article)
        val btnHomePage: Button = cusThis.findViewById(R.id.block_todo_btn_homepage)
        btnHomePage.setTextColor(ctx.getColor(R.color.black))
        btnCategory.setTextColor(ctx.getColor(R.color.black))
        btnAlgorithm.setTextColor(ctx.getColor(R.color.black))
        btnArticle.setTextColor(ctx.getColor(R.color.black))
        when (index) {
            ConstantUtils.INDEX_STUDY_HOME -> btnHomePage.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_QUESTION -> btnCategory.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_ALGORITHM -> btnAlgorithm.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_ARTICLE -> btnArticle.setTextColor(ctx.getColor(R.color.red))
        }
        //长按 选择 另一个 大模块
        btnHomePage.setOnLongClickListener {
            initModules(cusThis, ctx)
            true
        }
    }

    fun initNovelTabBar(index: Int, cusThis: Activity, ctx: Context) {
        val btnHomePage: Button = cusThis.findViewById(R.id.block_novel_btn_homepage)
        val btnCategory: Button = cusThis.findViewById(R.id.block_novel_btn_category)
        val btnAlgorithm: Button = cusThis.findViewById(R.id.block_novel_btn_algorithm)
        val btnArticle: Button = cusThis.findViewById(R.id.block_novel_btn_article)
        val btnSetting: Button = cusThis.findViewById(R.id.block_novel_btn_setting)
        btnHomePage.setTextColor(ctx.getColor(R.color.black))
        btnCategory.setTextColor(ctx.getColor(R.color.black))
        btnAlgorithm.setTextColor(ctx.getColor(R.color.black))
        btnArticle.setTextColor(ctx.getColor(R.color.black))
        when (index) {
            ConstantUtils.INDEX_STUDY_HOME -> btnHomePage.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_QUESTION -> btnCategory.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_ALGORITHM -> btnAlgorithm.setTextColor(ctx.getColor(R.color.red))
            ConstantUtils.INDEX_ARTICLE -> btnArticle.setTextColor(ctx.getColor(R.color.red))
        }
        //长按 选择 另一个 大模块
        btnHomePage.setOnLongClickListener {
            initModules(cusThis, ctx)
            true
        }
    }

    fun initModules(cusThis: Activity, ctx: Context) {
        val items = arrayOf("学习", "待办", "小说")
        val classItems = arrayOf(StudyHomePageActivity::class.java, TodoHomePageActivity::class.java,
            NovelHomePageActivity::class.java)
        val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(cusThis)
        alertBuilder.setTitle("切换列表")
        alertBuilder.setItems(items) { _, i ->
            Toast.makeText(cusThis, items[i], Toast.LENGTH_SHORT).show()
            //只有两个大模块直接切换
            //大于等于3个模块 弹出列表选择框 进行 选择
            //跳转到编辑详情页面
            val intent = Intent()
            intent.setClass(ctx, classItems[i])
            ctx.startActivity(intent)
        }
        alertBuilder.create().show()

    }
}