package org.unifiedpush.android.foss_embedded_fcm_distributor

/**
 * Constants as defined on the specs
 * https://unifiedpush.org/developers/spec/android/
 */

internal const val PREF_MASTER = "UP-embedded_fcm"
internal const val PREF_MASTER_TOKENS = "$PREF_MASTER:tokens"

internal const val ACTION_NEW_ENDPOINT = "org.unifiedpush.android.connector.NEW_ENDPOINT"
internal const val ACTION_REGISTRATION_FAILED = "org.unifiedpush.android.connector.REGISTRATION_FAILED"
internal const val ACTION_UNREGISTERED = "org.unifiedpush.android.connector.UNREGISTERED"
internal const val ACTION_MESSAGE = "org.unifiedpush.android.connector.MESSAGE"

internal const val ACTION_REGISTER = "org.unifiedpush.android.distributor.REGISTER"
internal const val ACTION_UNREGISTER = "org.unifiedpush.android.distributor.UNREGISTER"

internal const val EXTRA_TOKEN = "token"
internal const val EXTRA_ENDPOINT = "endpoint"
internal const val EXTRA_MESSAGE = "message"
internal const val EXTRA_BYTES_MESSAGE = "bytesMessage"
internal const val EXTRA_MESSAGE_ID = "id"
internal const val EXTRA_FCM_TOKEN = "FCMToken"
internal const val EXTRA_GET_ENDPOINT = "getEndpoint"


/*
 * FCM Related constants
 */
internal const val GSF_PACKAGE = "com.google.android.gms"

internal const val ACTION_FCM_TOKEN_REQUEST = "com.google.iid.TOKEN_REQUEST"
internal const val ACTION_FCM_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION"
internal const val ACTION_FCM_RECEIVE = "com.google.android.c2dm.intent.RECEIVE"

internal const val EXTRA_APPLICATION_PENDING_INTENT = "app"
/** Internal parameter used to indicate a 'subtype'  */
internal const val EXTRA_SUBTYPE = "subtype"

/** Extra used to indicate which senders (Google API project IDs) can send messages to the app  */
internal const val EXTRA_SENDER = "sender"
internal const val EXTRA_SCOPE = "scope"
internal const val EXTRA_KID = "kid"

internal const val EXTRA_REGISTRATION_ID = "registration_id"

internal const val KID_VALUE = "|ID|1|" // request ID?
