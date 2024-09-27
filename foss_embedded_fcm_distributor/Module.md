# Module foss_embedded_fcm_distributor

Embed a FCM distributor as a fallback if user don't have another distributor. Without Google proprietary blobs, may be less stable.

This library requires Android 4.1 or higher.

## Import the library

Add the dependency to the _module_ build.gradle. Replace {VERSION} with the [latest version](https://central.sonatype.com/artifact/org.unifiedpush.android/foss-embedded-fcm-distributor).

```groovy
dependencies {
    // ...
    implementation 'org.unifiedpush.android:foss-embedded-fcm-distributor:{VERSION}'
```

## Get your Google project number

Download `google-services.json` from the [firebase console](https://console.firebase.google.com/project/_/settings/serviceaccounts/adminsdk), and write down the value of "project_number".

## Expose a receiver

You need to expose a Receiver that extend [EmbeddedDistributorReceiver][org.unifiedpush.android.foss_embedded_fcm_distributor.EmbeddedDistributorReceiver]
and you must override [getEndpoint][org.unifiedpush.android.foss_embedded_fcm_distributor.EmbeddedDistributorReceiver.getEndpoint] to return the address of your FCM rewrite-proxy
and [googleProjectNumber][org.unifiedpush.android.foss_embedded_fcm_distributor.EmbeddedDistributorReceiver.googleProjectNumber] with the one from the `google-services.json`.

```kotlin
class EmbeddedDistributor: EmbeddedDistributorReceiver() {
    override val googleProjectNumber = "123456789012" // This value comes from the google-services.json

    override fun getEndpoint(context: Context, fcmToken: String, instance: String): String {
        // This returns the endpoint of your FCM Rewrite-Proxy
        return "https://<your.domain.tld>/FCM?v2&instance=$instance&token=$token"
    }
}
```

## Edit your manifest

The receiver has to be exposed in the `AndroidManifest.xml` in order to receive the UnifiedPush messages.

```xml
<receiver android:enabled="true"  android:name=".EmbeddedDistributor" android:exported="false">
    <intent-filter>
        <action android:name="org.unifiedpush.android.distributor.feature.BYTES_MESSAGE"/>
        <action android:name="org.unifiedpush.android.distributor.REGISTER"/>
        <action android:name="org.unifiedpush.android.distributor.UNREGISTER"/>
    </intent-filter>
</receiver>
```
