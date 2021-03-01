package org.unifiedpush.android.embedded_fcm_distributor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

interface GetEndpointHandler {
    fun getEndpoint(context: Context?, token: String, instance: String): String
}

/**
 * EMBEDDED FCM DISTRIBTUOR
 */
open class EmbeddedDistributorReceiver(private val handler: GetEndpointHandler) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val token = intent!!.getStringExtra(EXTRA_TOKEN)
        val instance = token?.let { getInstance(context!!, it) }
                ?: return
        when (intent.action) {
            ACTION_REGISTER -> {
                Log.d("UP-MessagingReceiver", "Fake Distributor register")
                saveGetEndpoint(context)
                val fcmToken = getFCMToken(context)
                fcmToken?.let {
                    sendNewEndpoint(context, token, it, instance)
                }
                FirebaseMessaging.getInstance().token.addOnSuccessListener { _fcmToken ->
                    Log.d("UP-Registration", "FCMToken: $_fcmToken")
                    saveFCMToken(context, _fcmToken)
                    sendNewEndpoint(context, token, _fcmToken, instance)
                }
            }
            ACTION_UNREGISTER -> {
                Log.d("UP-MessagingReceiver", "Fake Distributor unregister")
                val broadcastIntent = Intent()
                broadcastIntent.`package` = context!!.packageName
                broadcastIntent.action = ACTION_UNREGISTERED
                broadcastIntent.putExtra(EXTRA_TOKEN, token)
                context.sendBroadcast(broadcastIntent)
            }
        }
    }

    private fun saveGetEndpoint(context: Context?) {
        val prefs = context!!.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        val ff = 0xff.toChar().toString()
        prefs.edit().putString(EXTRA_GET_ENDPOINT,
            this@EmbeddedDistributorReceiver.handler.getEndpoint(context, "$ff$ff.TOKEN.$ff$ff", "$ff$ff.INSTANCE.$ff$ff")).commit()
    }
}
