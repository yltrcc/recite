package com.yltrcc.app.recite.activity
 
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.RemoteViews
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.service.MyService

class AppWidget:AppWidgetProvider() {
    companion object{
        private const val ACTION_BUTTON = "action_button"
         const val ACTION_UPDATE="action_update"
 
    }
    @SuppressLint("RemoteViewLayout")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if(intent==null||context==null){
            return
        }
        val action =intent.action
        //更新textview的text
        if (TextUtils.equals(action, ACTION_UPDATE)){
            val data=intent.getStringExtra("data")
            val remoteViews=RemoteViews(context.packageName, R.layout.appwidget_layout)
            remoteViews.setTextViewText(R.id.tv,data)
            val appWidgetManager=AppWidgetManager.getInstance(context)
            val componentName=ComponentName(context,AppWidget::class.java)
            appWidgetManager.updateAppWidget(componentName,remoteViews)
        }else if (action== ACTION_BUTTON){//启动服务，5秒内每秒更新一次数据
            val serviceIntent=Intent(context, MyService::class.java)
            context.startService(serviceIntent)
        }
 
    }
     /**
	 * 到达指定的更新时间或者当用户向桌面添加AppWidget时被调用
	 * appWidgetIds:桌面上所有的widget都会被分配一个唯一的ID标识，这个数组就是他们的列表
	 */
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (context!=null) {
            //此处的myBroadCast应该与AndroidManifest.xml文件中的保持一致
            val intent = Intent(ACTION_BUTTON)
            //在androi9.0以上版本需要设置包名
            intent.`package`=context.packageName
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            val remoteViews=RemoteViews(context.packageName,R.layout.appwidget_layout)
            remoteViews.setOnClickPendingIntent(R.id.btn,pendingIntent)
            appWidgetManager?.updateAppWidget(appWidgetIds,remoteViews)
        }
    }
}