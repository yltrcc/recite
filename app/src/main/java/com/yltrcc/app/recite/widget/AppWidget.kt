package com.yltrcc.app.recite.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.activity.study.StudyHomePageActivity


/**
 * 桌面小组件
 */
class AppWidget : AppWidgetProvider() {
    companion object {
        private const val ACTION_BUTTON = "action_button"
        const val ACTION_UPDATE = "action_update"

    }

    /**
     * 到达指定的更新时间或者当用户向桌面添加AppWidget时被调用
     * appWidgetIds:桌面上所有的widget都会被分配一个唯一的ID标识，这个数组就是他们的列表
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (context != null) {
//            //此处的myBroadCast应该与AndroidManifest.xml文件中的保持一致
//            val intent = Intent(ACTION_BUTTON)
//
//            //在androi9.0以上版本需要设置包名
//            intent.`package` = context.packageName
//            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//            val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_layout)
//            remoteViews.setOnClickPendingIntent(R.id.widget_btn_update, pendingIntent)
//            appWidgetManager?.updateAppWidget(appWidgetIds, remoteViews)
            //新intent

            //设置点击跳转 Activity
            val intent = Intent(context, StudyHomePageActivity::class.java)
            //创建一个pendingIntent。另外两个参数以后再讲。
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            //创建一个remoteViews。
            val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_layout)
            //绑定处理器，表示控件单击后，会启动pendingIntent。
            remoteViews.setOnClickPendingIntent(R.id.widget_btn_update, pendingIntent)
            appWidgetManager?.updateAppWidget(appWidgetIds, remoteViews)
        }
    }
}