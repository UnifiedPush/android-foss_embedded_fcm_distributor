package org.unifiedpush.android.embedded_fcm_distributor.fcm

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import org.unifiedpush.android.embedded_fcm_distributor.*
import org.unifiedpush.android.embedded_fcm_distributor.Utils.getDistributor
import org.unifiedpush.android.embedded_fcm_distributor.Utils.getToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.saveFCMToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.sendNewEndpoint

class FirebaseForwardingService : FirebaseMessagingService() {
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
        // The map can be used to allow applications keeping using their old gateway for FCM
        val message = Base64.decode(remoteMessage.data["b"], Base64.DEFAULT)
            ?: JSONObject(remoteMessage.data as Map<*, *>).toString().toByteArray()
        val instance = remoteMessage.data["i"] ?: INSTANCE_DEFAULT
        val intent = Intent()
        intent.action = ACTION_MESSAGE
        intent.setPackage(baseContext.packageName)
        intent.putExtra(EXTRA_MESSAGE, String(message))
        intent.putExtra(EXTRA_BYTES_MESSAGE, message)
        intent.putExtra(EXTRA_MESSAGE_ID, remoteMessage.messageId)
        intent.putExtra(EXTRA_TOKEN, getToken(baseContext, instance))
        baseContext.sendBroadcast(intent)
    }
}
