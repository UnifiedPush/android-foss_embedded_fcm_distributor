package org.unifiedpush.android.embedded_fcm_distributor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import org.unifiedpush.android.embedded_fcm_distributor.Utils.getFCMToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.getInstance
import org.unifiedpush.android.embedded_fcm_distributor.Utils.saveFCMToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.sendNewEndpoint

const val TAG = "UP-Embedded_distributor"

open class EmbeddedDistributorReceiver : BroadcastReceiver() {
    open fun getEndpoint(context: Context?, token: String, instance: String): String {
        return ""
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val token = intent!!.getStringExtra(EXTRA_TOKEN)
        val instance = token?.let { getInstance(context!!, it) }
                ?: return
        when (intent.action) {
            ACTION_REGISTER -> {
                Log.d(TAG, "Fake Distributor register")
                saveGetEndpoint(context)
                val fcmToken = getFCMToken(context)
                fcmToken?.let {
                    sendNewEndpoint(context, token, it, instance)
                }
                FirebaseMessaging.getInstance().token.addOnSuccessListener { _fcmToken ->
                    Log.d(TAG, "FCMToken: $_fcmToken")
                    saveFCMToken(context, _fcmToken)
                    sendNewEndpoint(context, token, _fcmToken, instance)
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Token received successfully")
                    } else {
                        Log.e(TAG, "FCMToken registration failed: " +
                                "${task.exception?.localizedMessage}")
                    }
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
            getEndpoint(context, "$ff$ff.TOKEN.$ff$ff", "$ff$ff.INSTANCE.$ff$ff")
        ).commit()
    }
}
