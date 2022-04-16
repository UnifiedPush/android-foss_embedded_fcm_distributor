package org.unifiedpush.android.embedded_fcm_distributor

/**
 * Constants as defined on the specs
 * https://github.com/UnifiedPush/UP-spec/blob/main/specifications.md
 */

const val PREF_MASTER = "UP-embedded_fcm"
const val PREF_MASTER_TOKENS = "$PREF_MASTER:tokens"

const val ACTION_NEW_ENDPOINT = "org.unifiedpush.android.connector.NEW_ENDPOINT"
const val ACTION_REGISTRATION_FAILED = "org.unifiedpush.android.connector.REGISTRATION_FAILED"
const val ACTION_UNREGISTERED = "org.unifiedpush.android.connector.UNREGISTERED"
const val ACTION_MESSAGE = "org.unifiedpush.android.connector.MESSAGE"

const val ACTION_REGISTER = "org.unifiedpush.android.distributor.REGISTER"
const val ACTION_UNREGISTER = "org.unifiedpush.android.distributor.UNREGISTER"

const val EXTRA_TOKEN = "token"
const val EXTRA_ENDPOINT = "endpoint"
const val EXTRA_MESSAGE = "message"
const val EXTRA_BYTES_MESSAGE = "bytesMessage"
const val EXTRA_FCM_TOKEN = "FCMToken"
const val EXTRA_GET_ENDPOINT = "getEndpoint"


/*
 * FCM Related constants
 */
const val GSF_PACKAGE = "com.google.android.gms"

const val ACTION_FCM_TOKEN_REQUEST = "com.google.iid.TOKEN_REQUEST"
const val ACTION_FCM_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION"
const val ACTION_FCM_RECEIVE = "com.google.android.c2dm.intent.RECEIVE"

const val EXTRA_APPLICATION_PENDING_INTENT = "app"
/** Internal parameter used to indicate a 'subtype'  */
const val EXTRA_SUBTYPE = "subtype"

/** Extra used to indicate which senders (Google API project IDs) can send messages to the app  */
const val EXTRA_SENDER = "sender"
const val EXTRA_SCOPE = "scope"
const val EXTRA_KID = "kid"

const val EXTRA_REGISTRATION_ID = "registration_id"

const val KID_VALUE = "|ID|1|" // request ID?
