package com.unwiringapps.earningnapp.repo

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.unwiringapps.earningnapp.Repo.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "onMessageReceived: message received with ${message.data}")
        Log.d(TAG, "onMessageReceived: notification data: ${message.notification}")
        val context = applicationContext

            if(message.notification != null && !message.notification?.title.isNullOrBlank() && !message.notification?.body.isNullOrBlank()) {
                NotificationRepository(context).deliverNotification(
                    NOTIFICATION_ID = message.notification.hashCode(),
                    title = message.notification?.title!!,
                    message = message.notification?.body!!,
                )
            }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "onNewToken: new token generated $p0")
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }
}