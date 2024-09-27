package org.unifiedpush.android.foss_embedded_fcm_distributor.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import org.unifiedpush.android.foss_embedded_fcm_distributor.*
import org.unifiedpush.android.foss_embedded_fcm_distributor.Utils.getTokens
import org.unifiedpush.android.foss_embedded_fcm_distributor.Utils.sendNewEndpoint
import java.util.Timer
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

/**
 * This receivers interacts with Google Services and receives FCM message. It is exposed by the library.
 */
class FirebaseReceiver : BroadcastReceiver() {

    private companion object {
        private const val TAG = "FirebaseReceiver"
        private val pendingMessages = mutableMapOf<String, ByteArray>()
    }

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

    private fun onNewToken(context: Context, fcmToken: String) {
        Log.d(TAG, "New FCM token: $fcmToken")
        getTokens(context).forEach {
            sendNewEndpoint(context, fcmToken, it)
        }
    }

    private fun onMessageReceived(context: Context, remoteMessage: Bundle) {
        Log.d(TAG, "New Firebase message")
        // Empty token can be used by app not using an UnifiedPush gateway.
        val token = remoteMessage.getString("i")
            ?: getTokens(context).lastOrNull() ?: return

        val messageId = remoteMessage.getString("google.message_id") ?: "null"

        getMessage(remoteMessage)?.let { message ->
            forwardMessage(context, token, message, messageId)
        }
    }

    private fun forwardMessage(context: Context,
                               token: String,
                               message: ByteArray,
                               messageId: String) {
        val intent = Intent()
        intent.action = ACTION_MESSAGE
        intent.setPackage(context.packageName)
        intent.putExtra(EXTRA_MESSAGE, String(message))
        intent.putExtra(EXTRA_BYTES_MESSAGE, message)
        intent.putExtra(EXTRA_MESSAGE_ID, messageId)
        intent.putExtra(EXTRA_TOKEN, token)
        context.sendBroadcast(intent)
    }

    private fun getMessage(data: Bundle): ByteArray? {
        var message: ByteArray? = null
        data.getString("b")?.let { b64 ->
            if (!Regex("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")
                    .matches(b64)) {
                // The map can be used to allow applications keeping using their old gateway for FCM
                return JSONObject(bundleToMap(data)).toString().toByteArray()
            }
            data.getString("m")?.let { mId ->
                data.getString("s")?.let { splitId ->
                    if (pendingMessages.containsKey(mId)) {
                        Log.d(TAG, "Found pending message")
                        message = when (splitId) {
                            "1" -> {
                                Base64.decode(b64, Base64.DEFAULT) +
                                        (pendingMessages[mId] ?: ByteArray(0))
                            }
                            "2" -> {
                                (pendingMessages[mId] ?: ByteArray(0)) +
                                        Base64.decode(b64, Base64.DEFAULT)
                            }
                            else -> ByteArray(0)
                        }
                        pendingMessages.remove(mId)
                    } else {
                        pendingMessages[mId] = Base64.decode(b64, Base64.DEFAULT)
                        Timer().schedule(3000) {
                            pendingMessages.remove(mId)
                        }
                    }
                }
            } ?: run {
                return Base64.decode(b64, Base64.DEFAULT)
            }
        } ?: run {
            // The map can be used to allow applications keeping using their old gateway for FCM
            return JSONObject(bundleToMap(data)).toString().toByteArray()
        }
        return message
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
