package com.yltrcc.app.recite.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.yltrcc.app.recite.service.MyService
import com.yltrcc.app.recite.service.NotificationService
import java.util.*

class AlarmManagerUtils(aContext: Context) {
    //闹钟执行任务的时间间隔
    private val TIME_INTERVAL = (10 * 1000 ).toLong()
    public var context: Context? = aContext
    var am: AlarmManager? = null
    var pendingIntent: PendingIntent? = null

    private var calendar: Calendar? = null


    fun init() {
        am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyService::class.java)
        pendingIntent = PendingIntent.getService(context, 0, intent, 0) //每隔10秒启动一次服务
    }

    fun getUpAlarmManagerStartWork() {
        /*calendar = Calendar.getInstance()
        calendar!!.set(Calendar.HOUR_OF_DAY, 0)
        calendar!!.set(Calendar.MINUTE, 15)
        calendar!!.set(Calendar.SECOND, 0)*/

        //版本适配 System.currentTimeMillis()
        // 6.0及以上
        am!!.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + (1 * 1000 ).toLong(), pendingIntent
        )
    }
    fun setUpAlarmManagerStartWork(calendar: Calendar) {

        //版本适配 System.currentTimeMillis()
        // 6.0及以上
        val intent = Intent(context, NotificationService::class.java)

        am!!.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis, PendingIntent.getService(context, 0, intent, 0)
        )
    }

    fun getUpAlarmManagerWorkOnOthers() {
        //高版本重复设置闹钟达到低版本中setRepeating相同效果
        // 6.0及以上
        if (am == null) {
            am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        am!!.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + TIME_INTERVAL, pendingIntent
        )
    }
}