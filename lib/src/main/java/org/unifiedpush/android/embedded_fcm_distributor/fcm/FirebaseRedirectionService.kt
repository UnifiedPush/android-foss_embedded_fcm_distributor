package org.unifiedpush.android.embedded_fcm_distributor.fcm

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.unifiedpush.android.embedded_fcm_distributor.*

class FirebaseRedirectionService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("UP-FCM", "Firebase onNewToken $token")
        if (getDistributor(baseContext) == packageName) {
            saveFCMToken(baseContext, token)
            val prefs = baseContext.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
            val instances = prefs.getStringSet(PREF_MASTER_INSTANCE, null)?: emptySet<String>().toMutableSet()
            instances.forEach {
                sendNewEndpoint(baseContext, getToken(baseContext, it), token, it)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("UP-FCM", "Firebase onMessageReceived ${remoteMessage.messageId}")
        val message = remoteMessage.data["body"]
        val instance = remoteMessage.data["instance"] ?: INSTANCE_DEFAULT
        val intent = Intent()
        intent.action = ACTION_MESSAGE
        intent.setPackage(baseContext.packageName)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_MESSAGE_ID, remoteMessage.messageId)
        intent.putExtra(EXTRA_TOKEN, getToken(baseContext, instance))
        baseContext.sendBroadcast(intent)
    }
}
