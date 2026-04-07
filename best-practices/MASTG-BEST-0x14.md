---
title: Use Explicit Intents for Internal IPC
alias: use-explicit-intents-for-internal-ipc
id: MASTG-BEST-0x14
platform: android
knowledge: [MASTG-KNOW-0025]
---

Use [explicit intents](https://developer.android.com/guide/components/intents-filters#ExplicitIntent) when communicating between components within the same app. An explicit intent specifies the target component directly by package name or class name, ensuring the intent can only be delivered to the intended recipient and can't be intercepted by a third-party app.

## Java/Kotlin

Set the target package or component explicitly before sending the intent:

```kotlin
// Explicit by package — restricts delivery to your own app
val intent = Intent("com.example.app.PROCESS_DATA").apply {
    setPackage("com.example.app")
    putExtra("key", "value")
}
startActivity(intent)

// Explicit by component — the most restrictive form
val intent = Intent(context, TargetActivity::class.java).apply {
    putExtra("key", "value")
}
startActivity(intent)
```

Never send sensitive data (tokens, credentials, API keys) in an implicit intent. Any installed app that registers a matching `<intent-filter>` can receive the intent and all its extras.

## Manifest Configuration

Avoid declaring internal components with `android:exported="true"` and a custom `<intent-filter>` action. If a component is only needed internally, either:

- Set `android:exported="false"` (the default when no `<intent-filter>` is present), or
- Remove the `<intent-filter>` entirely and use an explicit intent to reach the component.

```xml
<!-- Avoid: exported with custom action — any app can send this intent -->
<activity android:name=".InternalActivity" android:exported="true">
    <intent-filter>
        <action android:name="com.example.app.INTERNAL_ACTION" />
    </intent-filter>
</activity>

<!-- Prefer: not exported, reachable only by explicit intent -->
<activity android:name=".InternalActivity" android:exported="false" />
```

!!! note

     Setting `android:exported="false"` doesn't prevent other components within the same app from starting the activity via an explicit intent. It only prevents external apps from doing so. If `android:exported="true"` is required (for example, to handle system actions), restrict access using [permissions](https://developer.android.com/guide/topics/permissions/overview) with `android:permission`.
