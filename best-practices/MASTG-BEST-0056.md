---
title: Use Explicit Intents for Internal IPC
alias: use-explicit-intents-for-internal-ipc
id: MASTG-BEST-0056
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

For internal components, ensure they are not inadvertently exposed to other applications. For detailed instructions on properly securing the `AndroidManifest.xml`, refer to @MASTG-BEST-0052.
