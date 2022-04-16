package org.unifiedpush.android.embedded_fcm_distributor.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import org.unifiedpush.android.embedded_fcm_distributor.*
import org.unifiedpush.android.embedded_fcm_distributor.Utils.getTokens
import org.unifiedpush.android.embedded_fcm_distributor.Utils.saveFCMToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.sendNewEndpoint


private const val TAG = "FirebaseReceiver"

class FirebaseReceiver : BroadcastReceiver() {

    private fun bundleToMap(extras: Bundle): Map<String, *> {
        val map: MutableMap<String, Any?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = extras.get(key)
            map[key] = if (value is Bundle) {
                bundleToMap(value)
            } else {
                value
            }
        }
        return map
    }

    private fun onNewToken(context: Context, FCMToken: String) {
        Log.d(TAG, "New FCM token: $FCMToken")
        saveFCMToken(context, FCMToken)
        getTokens(context).forEach {
            sendNewEndpoint(context, FCMToken, it)
        }
    }

    private fun onMessageReceived(context: Context, remoteMessage: Bundle) {
        Log.d(TAG, "New Firebase message")
        // The map can be used to allow applications keeping using their old gateway for FCM
        val message = remoteMessage.getString("b")?.let {
            Base64.decode(it, Base64.DEFAULT)
        } ?: JSONObject(bundleToMap(remoteMessage)).toString().toByteArray()
        // Empty token can be used by app not using an UnifiedPush gateway.
        val token = remoteMessage.getString("i")
            ?: getTokens(context).last()
        val intent = Intent()
        intent.action = ACTION_MESSAGE
        intent.setPackage(context.packageName)
        intent.putExtra(EXTRA_MESSAGE, String(message))
        intent.putExtra(EXTRA_BYTES_MESSAGE, message)
        intent.putExtra(EXTRA_TOKEN, token)
        context.sendBroadcast(intent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_FCM_REGISTRATION -> {
                if (intent.hasExtra(EXTRA_REGISTRATION_ID)) {
                    var deviceToken = intent.getStringExtra(EXTRA_REGISTRATION_ID) ?: ""
                    if (deviceToken.startsWith(KID_VALUE)) deviceToken =
                        deviceToken.substring(KID_VALUE.length + 1)
                    onNewToken(context, deviceToken)
                    Log.i(
                        TAG,
                        "Successfully registered for FCM"
                    )
                } else {
                    Log.e(
                        TAG,
                        "FCM registration intent did not contain registration_id: $intent"
                    )
                    val extras = intent.extras
                    for (key in extras!!.keySet()) {
                        Log.i(
                            TAG,
                            key + " -> " + extras[key]
                        )
                    }
                }
            }
             ACTION_FCM_RECEIVE -> {
                 onMessageReceived(context, intent.extras!!)
            }
        }
    }
}