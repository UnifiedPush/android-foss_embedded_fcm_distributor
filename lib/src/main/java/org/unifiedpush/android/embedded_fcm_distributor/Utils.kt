package org.unifiedpush.android.embedded_fcm_distributor

import android.content.Context
import android.content.Intent

object Utils {
    fun getFCMToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        return prefs.getString(EXTRA_FCM_TOKEN, null)
    }

    fun saveFCMToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        prefs.edit().putString(EXTRA_FCM_TOKEN, token).commit()
    }

    fun getTokens(context: Context): MutableSet<String> {
        val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        return prefs.getStringSet(PREF_MASTER_TOKENS, null)
            ?: mutableSetOf()
    }

    fun saveToken(context: Context, token: String) {
        val tokens = getTokens(context)
        if (!tokens.contains(token)) {
            tokens.add(token)
            val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
            prefs.edit().putStringSet(PREF_MASTER_TOKENS, tokens).commit()
        }
    }

    fun removeToken(context: Context, token: String) {
        val tokens = getTokens(context)
        if (tokens.contains(token)) {
            tokens.remove(token)
            val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
            prefs.edit().putStringSet(PREF_MASTER_TOKENS, tokens).commit()
        }
    }

    fun sendNewEndpoint(
        context: Context,
        fcmToken: String,
        connectionToken: String
    ) {
        val broadcastIntent = Intent()
        broadcastIntent.`package` = context.packageName
        broadcastIntent.action = ACTION_NEW_ENDPOINT
        broadcastIntent.putExtra(EXTRA_ENDPOINT, getEndpoint(context, fcmToken, connectionToken))
        broadcastIntent.putExtra(EXTRA_TOKEN, connectionToken)
        context.sendBroadcast(broadcastIntent)
    }

    private fun getEndpoint(context: Context, fcmToken: String, instance: String): String? {
        val prefs = context.getSharedPreferences(PREF_MASTER, Context.MODE_PRIVATE)
        val ff = 0xff.toChar().toString()
        return prefs.getString(EXTRA_GET_ENDPOINT, null)
            ?.replace("$ff$ff.TOKEN.$ff$ff", fcmToken)
            ?.replace("$ff$ff.INSTANCE.$ff$ff", instance)
    }
}