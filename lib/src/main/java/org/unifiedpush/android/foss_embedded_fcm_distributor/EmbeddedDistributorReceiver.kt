package org.unifiedpush.android.foss_embedded_fcm_distributor

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import org.unifiedpush.android.foss_embedded_fcm_distributor.Utils.removeToken
import org.unifiedpush.android.foss_embedded_fcm_distributor.Utils.saveToken
import org.unifiedpush.android.foss_embedded_fcm_distributor.Utils.sendRegistrationFailed

private const val TAG = "UP-Embedded_distributor"

open class EmbeddedDistributorReceiver : BroadcastReceiver() {
    open fun getEndpoint(context: Context, token: String, instance: String): String {
        return ""
    }

    open val googleProjectNumber = "0000"

    override fun onReceive(context: Context, intent: Intent) {
        val token = intent.getStringExtra(EXTRA_TOKEN) ?: return
        Log.d(TAG, "New intent for $token")
        when (intent.action) {
            ACTION_REGISTER -> {
                Log.d(TAG, "Registering to the embedded distributor")
                if (!isPlayServicesAvailable(context)) {
                    sendRegistrationFailed(context, token, "PlayServices not available")
                }
                saveGetEndpoint(context)
                saveToken(context, token)
                registerFCM(context)
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

    private fun isPlayServicesAvailable(context: Context): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo("com.google.android.gms", PackageManager.GET_ACTIVITIES)
            return true

        } catch (e: PackageManager.NameNotFoundException) {
            Log.v(TAG, e.message!!)
        }
        return false
    }

    private fun registerFCM(context: Context) {
        val intent = Intent(ACTION_FCM_TOKEN_REQUEST)
        intent.setPackage(GSF_PACKAGE)
        intent.putExtra(
            EXTRA_APPLICATION_PENDING_INTENT,
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        intent.putExtra(EXTRA_SENDER, googleProjectNumber)
        intent.putExtra(EXTRA_SUBTYPE, googleProjectNumber)
        intent.putExtra(EXTRA_SCOPE, "*")
        intent.putExtra(EXTRA_KID, KID_VALUE)
        context.sendBroadcast(intent)
    }

    private fun saveGetEndpoint(context: Context) {
        val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        val ff = 0xff.toChar().toString()
        prefs.edit().putString(EXTRA_GET_ENDPOINT,
            getEndpoint(context, "$ff$ff.TOKEN.$ff$ff", "$ff$ff.INSTANCE.$ff$ff")
        ).commit()
    }
}
