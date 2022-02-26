package org.unifiedpush.android.embedded_fcm_distributor.fcm

import android.content.Intent
import android.util.Base64
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import org.unifiedpush.android.embedded_fcm_distributor.*
import org.unifiedpush.android.embedded_fcm_distributor.Utils.getTokens
import org.unifiedpush.android.embedded_fcm_distributor.Utils.saveFCMToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.sendNewEndpoint

private const val TAG = "FirebaseForwarding"

class FirebaseForwardingService : FirebaseMessagingService() {
    override fun onNewToken(FCMToken: String) {
        Log.d(TAG, "New FCM token: $FCMToken")
        saveFCMToken(baseContext, FCMToken)
        getTokens(baseContext).forEach {
            sendNewEndpoint(baseContext, FCMToken, it)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "New Firebase message ${remoteMessage.messageId}")
        // The map can be used to allow applications keeping using their old gateway for FCM
        val message = remoteMessage.data["b"]?.let {
            Base64.decode(it, Base64.DEFAULT)
        } ?: JSONObject(remoteMessage.data as Map<*, *>).toString().toByteArray()
        // Empty token can be used by app not using an UnifiedPush gateway.
        val token = remoteMessage.data["i"] ?: ""
        val intent = Intent()
        intent.action = ACTION_MESSAGE
        intent.setPackage(baseContext.packageName)
        intent.putExtra(EXTRA_MESSAGE, String(message))
        intent.putExtra(EXTRA_BYTES_MESSAGE, message)
        intent.putExtra(EXTRA_MESSAGE_ID, remoteMessage.messageId)
        intent.putExtra(EXTRA_TOKEN, token)
        baseContext.sendBroadcast(intent)
    }
}
