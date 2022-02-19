package org.unifiedpush.android.embedded_fcm_distributor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import org.unifiedpush.android.embedded_fcm_distributor.Utils.getFCMToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.removeToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.saveFCMToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.saveToken
import org.unifiedpush.android.embedded_fcm_distributor.Utils.sendNewEndpoint

private const val TAG = "UP-Embedded_distributor"

open class EmbeddedDistributorReceiver : BroadcastReceiver() {
    open fun getEndpoint(context: Context, token: String, instance: String): String {
        return ""
    }

    override fun onReceive(context: Context, intent: Intent) {
        val token = intent.getStringExtra(EXTRA_TOKEN)!!
        Log.d(TAG, "New intent for $token")
        when (intent.action) {
            ACTION_REGISTER -> {
                Log.d(TAG, "Registering to the embedded distributor")
                saveGetEndpoint(context)
                saveToken(context, token)
                getFCMToken(context)?.let {
                    sendNewEndpoint(context, it, token)
                    return
                }
                FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                    Log.d(TAG, "New FCMToken: $fcmToken")
                    saveFCMToken(context, fcmToken)
                    sendNewEndpoint(context, fcmToken, token)
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Token successfully received")
                    } else {
                        Log.e(TAG, "FCMToken registration failed: " +
                                "${task.exception?.localizedMessage}")
                    }
                }
            }
            ACTION_UNREGISTER -> {
                Log.d(TAG, "Fake Distributor unregister")
                removeToken(context, token)
                val broadcastIntent = Intent()
                broadcastIntent.`package` = context.packageName
                broadcastIntent.action = ACTION_UNREGISTERED
                broadcastIntent.putExtra(EXTRA_TOKEN, token)
                context.sendBroadcast(broadcastIntent)
            }
        }
    }

    private fun saveGetEndpoint(context: Context) {
        val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        val ff = 0xff.toChar().toString()
        prefs.edit().putString(EXTRA_GET_ENDPOINT,
            getEndpoint(context, "$ff$ff.TOKEN.$ff$ff", "$ff$ff.INSTANCE.$ff$ff")
        ).commit()
    }
}
