---
title: Restrict Access to Android Services
alias: restrict-access-to-services
id: MASTG-BEST-0x02
platform: android
knowledge: [MASTG-KNOW-0x02, MASTG-KNOW-0017]
---

Expose a [service](https://developer.android.com/guide/components/services) to other apps only when another app genuinely needs to start or bind to it. Every exported service is an entry point that any app on the device can invoke, so keeping services private by default reduces the app's attack surface.

- **Set `android:exported` explicitly.** Declare [`android:exported="false"`](https://developer.android.com/guide/topics/manifest/service-element#exported) on every service that doesn't need to be started or bound to by other apps. Don't rely on the default value: it has changed across Android versions and component types, and an [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) historically makes a service exported unless you set the attribute. Since Android 12 (API level 31), any component with an intent filter must set `android:exported` explicitly. See [`android:exported`](https://developer.android.com/privacy-and-security/risks/android-exported). For background on services and the `android:exported` attribute, see @MASTG-KNOW-0x02.
- **Require an effective permission when appropriate.** If only specific apps should start or bind to the service, protect it with [`android:permission`](https://developer.android.com/guide/topics/manifest/service-element#prmsn) and define a custom permission that matches the intended trust boundary. For example, use `android:protectionLevel="signature"` to limit access to apps signed with the same key. Do not treat the presence of `android:permission` as sufficient by itself: a broadly grantable custom permission, such as `normal` or `dangerous`, might still allow untrusted apps to start or bind to sensitive services. See @MASTG-KNOW-0017 for Android permission protection levels and custom permissions.
- **Verify the caller inside the service.** For bound services that expose a `Messenger` or [AIDL](https://developer.android.com/guide/components/aidl) interface, check the caller's permission at runtime with [`Context.checkCallingPermission`](https://developer.android.com/reference/android/content/Context#checkCallingPermission(java.lang.String)) or [`Context.enforceCallingPermission`](https://developer.android.com/reference/android/content/Context#enforceCallingPermission(java.lang.String,%20java.lang.String)) before performing sensitive operations. See [Binder and Messenger interfaces](https://developer.android.com/privacy-and-security/security-tips#binder-and-messenger-interfaces).
- **Don't expose sensitive functionality through an unprotected interface.** Make sure an exported service doesn't let a caller change security-relevant state (for example, credentials or PINs) or retrieve sensitive data without authorization.
- **Validate all transaction inputs.** Treat the contents of every incoming `Message` or AIDL call as untrusted and validate them before use.

For background on services and their access-control attributes, see @MASTG-KNOW-0x02. General guidance is available in the [security checklist](https://developer.android.com/privacy-and-security/security-tips#Services).
