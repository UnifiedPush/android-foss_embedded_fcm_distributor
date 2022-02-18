package org.unifiedpush.android.embedded_fcm_distributor

import android.content.Context
import android.content.Intent

object Utils {
    fun getFCMToken(context: Context?): String? {
        val prefs = context!!.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        return prefs.getString(EXTRA_FCM_TOKEN, null)
    }

    fun getDistributor(context: Context): String {
        return context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)?.getString(
            PREF_MASTER_DISTRIBUTOR, ""
        ) ?: ""
    }

    fun getToken(context: Context, instance: String): String {
        return context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)?.getString(
            "$instance/${PREF_MASTER_TOKEN}", null
        ) ?: ""
    }

    fun saveFCMToken(context: Context?, token: String) {
        val prefs = context!!.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        prefs.edit().putString(EXTRA_FCM_TOKEN, token).commit()
    }

    fun sendNewEndpoint(
        context: Context?,
        connectionToken: String,
        fcmToken: String,
        instance: String
    ) {
        val broadcastIntent = Intent()
        broadcastIntent.`package` = context!!.packageName
        broadcastIntent.action = ACTION_NEW_ENDPOINT
        broadcastIntent.putExtra(EXTRA_ENDPOINT, getEndpoint(context, fcmToken, instance))
        broadcastIntent.putExtra(EXTRA_TOKEN, connectionToken)
        context.sendBroadcast(broadcastIntent)
    }

    fun getInstance(context: Context, token: String): String? {
        val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        val instances =
            prefs.getStringSet(PREF_MASTER_INSTANCE, null) ?: emptySet<String>().toMutableSet()
        instances.forEach {
            if (prefs.getString("$it/${PREF_MASTER_TOKEN}", "").equals(token)) {
                return it
            }
        }
        return null
    }

    private fun getEndpoint(context: Context?, token: String, instance: String): String? {
        val prefs = context!!.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        val ff = 0xff.toChar().toString()
        return prefs.getString(EXTRA_GET_ENDPOINT, null)
            ?.replace("$ff$ff.TOKEN.$ff$ff", token)
            ?.replace("$ff$ff.INSTANCE.$ff$ff", instance)
    }
}