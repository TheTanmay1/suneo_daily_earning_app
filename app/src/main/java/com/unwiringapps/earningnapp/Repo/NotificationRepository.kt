package com.unwiringapps.earningnapp.Repo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.unwiringapps.earningnapp.MainActivity
import com.unwiringapps.earningnapp.R

class NotificationRepository(val context: Context) {
    private val mNotificationManager: NotificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

    fun createNotificationChannel() {
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Stand up notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = context.getColor(R.color.quiz_blue)
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Messages from developer"
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun makeNotificationBuilder(
        contentPendingIntent: PendingIntent,
        title: String,
        message: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.newupdateonelogo)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
    }

    fun deliverNotification(
        NOTIFICATION_ID: Int,
        title: String,
        message: String
    ) {
        Log.d(
            TAG,
            "deliverNotification: delivering notification with, title: $title; message: $message"
        )
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder: NotificationCompat.Builder =
            makeNotificationBuilder(contentPendingIntent, message = message, title = title)
        mNotificationManager.notify(NOTIFICATION_ID, builder.build())
    }


    companion object {
        private const val PRIMARY_CHANNEL_ID = "suneo_notification_channel"
        private const val TAG = "NotificationRepository"

    }
}