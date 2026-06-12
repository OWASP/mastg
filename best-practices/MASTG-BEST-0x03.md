---
title: Restrict Access to Android Broadcast Receivers
alias: restrict-access-to-broadcast-receivers
id: MASTG-BEST-0x03
platform: android
knowledge: [MASTG-KNOW-0x03, MASTG-KNOW-0017]
---

Expose a [broadcast receiver](https://developer.android.com/guide/components/broadcasts) to other apps only when it genuinely needs to receive broadcasts from them, and don't send sensitive data in broadcasts that other apps can read. Both the receiving and sending sides of a broadcast are part of the app's attack surface.

- **Set `android:exported` explicitly.** Declare [`android:exported="false"`](https://developer.android.com/guide/topics/manifest/receiver-element#exported) on every manifest receiver that doesn't need to receive broadcasts from other apps. Don't rely on the default value: it has changed across Android versions and component types, and an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) historically makes a receiver exported unless you set the attribute. Since Android 12 (API level 31), any component with an intent filter must set `android:exported` explicitly. See [`android:exported`](https://developer.android.com/privacy-and-security/risks/android-exported). For background on broadcast receivers and the `android:exported` attribute, see @MASTG-KNOW-0x03.
- **Mark context-registered receivers correctly.** When registering a receiver at runtime with [`Context.registerReceiver`](https://developer.android.com/reference/android/content/Context#registerReceiver(android.content.BroadcastReceiver,%20android.content.IntentFilter)), pass `RECEIVER_NOT_EXPORTED` unless the receiver must accept broadcasts from other apps. This is required since Android 13 (API level 33) for receivers of non-system broadcasts.
- **Require an effective permission when appropriate.** If only specific apps should send broadcasts to the receiver, protect it with [`android:permission`](https://developer.android.com/guide/topics/manifest/receiver-element#prmsn) and define a custom permission that matches the intended trust boundary. For example, use `android:protectionLevel="signature"` to limit access to apps signed with the same key. Do not treat the presence of `android:permission` as sufficient by itself: a broadly grantable custom permission, such as `normal` or `dangerous`, might still allow untrusted apps to send broadcasts to sensitive receivers. See @MASTG-KNOW-0017 for Android permission protection levels and custom permissions.
- **Don't broadcast sensitive data implicitly.** Don't put sensitive data (for example, credentials, tokens, or personal information) in an implicit broadcast. Send it with an explicit target package or component, or use an in-app mechanism such as [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData) instead of a system broadcast. See [Security considerations and best practices](https://developer.android.com/guide/components/broadcasts#security-and-best-practices).
- **Avoid sticky broadcasts.** Don't use the deprecated sticky broadcast methods; they persist after delivery and provide no access control. See [Sticky broadcasts](https://developer.android.com/privacy-and-security/risks/sticky-broadcast).
- **Validate incoming intent data.** Treat all data in a received `Intent` as untrusted and validate it before use.

For background on broadcast receivers and their access-control attributes, see @MASTG-KNOW-0x03.
