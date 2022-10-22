package com.yltrcc.app.recite.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.yltrcc.app.recite.R
import com.yltrcc.app.recite.activity.MainActivity


class NotificationService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Thread {
            Log.d(TAG, "run: ")
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //通知点击事项
            //通知点击事项
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            val notification: Notification = NotificationCompat.Builder(this, "chat")
                .setContentTitle("定时通知：发送微信公众号文章")
                .setContentText("每天中午定时发送微信公众号文章")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.diannao)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.diannao))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
            manager.notify(1, notification)
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}