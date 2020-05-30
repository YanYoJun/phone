package com.plbear.phone.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.telephony.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.plbear.phone.R

/**
 * @author：wangzhicheng
 * @date: 2018/8/17 09:34
 * @email： wangzhicheng@lukou.com
 */
class ForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        setForeground()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        val intent = Intent(applicationContext, ForegroundService::class.java)
        startService(intent)
    }

    private fun setForeground() {
        //前台 service
        startForeground(FOREGROUND_ID, buildForegroundNotification("你正在被密切监控", "监控中..."))
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }



    private fun buildForegroundNotification(contentTitle: String, contentText: String): Notification {
        val channelId = "1234343434343"
        val mBuilder = NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setAutoCancel(false)
                .setLights(Color.GREEN, 1000, 2000)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        return mBuilder.build()
    }

    companion object {
        private const val FOREGROUND_ID = 100
        fun start(context: Context) {
            val intent = Intent(context, ForegroundService::class.java)
            context.startService(intent)
        }
    }

}
