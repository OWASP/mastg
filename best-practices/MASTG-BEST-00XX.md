---
title: Use Verified Android App Links for Deep Links
alias: use-verified-android-app-links
id: MASTG-BEST-00XX
platform: android
knowledge: [MASTG-KNOW-0019]
---

Use [Android App Links](https://developer.android.com/training/app-links) for any deep link that handles sensitive operations (e.g., OAuth callbacks, password resets, payment confirmations). App Links are the only deep link mechanism on Android that is verified by the OS and cannot be hijacked by another app.

## Why App Links

Android supports two types of deep links (see @MASTG-KNOW-0019):

- **Custom URL schemes** (e.g., `myapp://`) — not verified by the OS. Any app can register the same scheme and silently intercept your intents.
- **Android App Links** (e.g., `https://example.com/...`) — verified by the OS via a [Digital Asset Links](https://developers.google.com/digital-asset-links/v1/getting-started) file hosted on your domain. Only your app can handle them.

Without App Links, your app is exposed to **deep link hijacking**: a malicious app installed on the same device can register an identical intent filter and steal OAuth tokens, session data, or trigger unintended actions on behalf of the user. On Android versions prior to Android 12 (API level 31), the risk is even greater—any non-verifiable link in the manifest (including custom URL schemes or `http/https` links missing `autoVerify`) causes the OS to skip verification for **all** App Links declared by your app.

## How to Implement App Links

App Links require two things: the correct intent filter in your manifest and a Digital Asset Links file served from your domain.

### 1. Declare the intent filter with `android:autoVerify="true"`

The `android:autoVerify="true"` attribute tells the OS to verify your domain ownership at install time. Without it, the link is treated as a regular, unverified deep link.

```xml
<activity android:name=".MyCallbackActivity">
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https"
              android:host="www.example.com"
              android:path="/callback" />
    </intent-filter>
</activity>
```

### 2. Host a Digital Asset Links file on your domain

Publish `assetlinks.json` at `https://www.example.com/.well-known/assetlinks.json`, served over HTTPS without redirects:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.example.myapp",
    "sha256_cert_fingerprints": ["<YOUR_APP_SIGNING_CERT_SHA256>"]
  }
}]
```

## References

- [Android App Links](https://developer.android.com/training/app-links)
- [Verify Android App Links](https://developer.android.com/training/app-links/verify-android-applinks)
- [Digital Asset Links](https://developers.google.com/digital-asset-links/v1/getting-started)
