---
title: Restrict Access to Android App Components
alias: restrict-access-to-android-components
id: MASTG-BEST-0052
platform: android
knowledge: [MASTG-KNOW-0017, MASTG-KNOW-0132, MASTG-KNOW-0133, MASTG-KNOW-0134, MASTG-KNOW-0020]
---

Only export an app component when another app genuinely needs to interact with it. Every exported component is an entry point that other apps on the device may be able to invoke, so keeping components private by default reduces the app's attack surface.

## Set the exported status explicitly

Declare `android:exported="false"` on every manifest-declared component that doesn't need to be accessible to other apps. Don't rely on the default value: it has changed across Android versions and component types, and an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) historically makes a component exported unless the attribute is set explicitly. Since Android 12 (API level 31), any activity, service, or broadcast receiver with an intent filter [must explicitly declare `android:exported`](https://developer.android.com/privacy-and-security/risks/android-exported).

For context-registered receivers, when registering the receiver with a `registerReceiver()` overload that accepts flags, explicitly pass `RECEIVER_NOT_EXPORTED` in the `flags` parameter unless the receiver must accept broadcasts from other apps. Use `RECEIVER_EXPORTED` only when external broadcasts are required.

Setting `android:exported="false"` (for manifest-declared components) or `RECEIVER_NOT_EXPORTED` (for context-registered receivers) prevents ordinary apps, including separate apps from the same developer, from accessing the component. Components within the same app, [apps sharing the same user ID](https://developer.android.com/guide/topics/permissions/defining#userid), and privileged system components can still reach it. If the component is only used internally, remove its `<intent-filter>` and use an explicit intent to reach it. Use `android:exported="true"` with an appropriate permission, usually `signature`, when a separate trusted app must access the component.

## Require an effective permission when appropriate

If a component must be exported but should only be accessible to specific apps, protect it with `android:permission` and use a permission with a protection level appropriate for the intended callers. A custom permission with `signature` protection is usually appropriate when access should be limited to apps signed with the same key.

Do not treat the presence of `android:permission` as sufficient by itself: a broadly grantable protection level (`normal` or `dangerous`) may still allow untrusted apps to invoke sensitive components. See @MASTG-KNOW-0017 for Android permission protection levels and custom permissions.

For context-registered receivers, when registering an exported receiver that should only accept broadcasts from specific apps, pass an appropriate permission in the `broadcastPermission` parameter. This restricts which apps can send broadcasts to the receiver. The permission should resolve to a trusted `<permission>` declaration with an appropriate protection level, usually `signature`.

For example, define the permission in the manifest of the app that owns the receiver or in another trusted package:

```xml
<permission
    android:name="com.example.app.permission.SEND_INTERNAL_BROADCAST"
    android:protectionLevel="signature" />
```

Then require that permission when registering the receiver:

```kotlin
registerReceiver(
    myReceiver,
    IntentFilter("com.example.app.ACTION_INTERNAL"),
    "com.example.app.permission.SEND_INTERNAL_BROADCAST",
    null,
    Context.RECEIVER_EXPORTED
)
```
