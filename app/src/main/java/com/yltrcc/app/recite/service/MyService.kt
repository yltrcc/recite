package com.yltrcc.app.recite.service
 
import android.app.IntentService
import android.content.Intent
import com.yltrcc.app.recite.activity.AppWidget

class MyService :IntentService("MyService") {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onHandleIntent(p0: Intent?) {
        for (i in 0 until 5){
            Thread.sleep(1000)
            val intent=Intent(AppWidget.ACTION_UPDATE)
            intent.`package`=this.packageName
            intent.putExtra("data", "data$i")
            sendBroadcast(intent)
        }
    }
}