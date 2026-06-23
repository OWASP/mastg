---
title: Verify Android App Links with autoVerify and Digital Asset Links
alias: verify-android-app-links-with-autoverify-and-digital-asset-links
id: MASTG-BEST-0070
platform: android
knowledge: [MASTG-KNOW-0019]
available_since: 23
---

When your app handles `http`/`https` deep links, declare them as [Android App Links](https://developer.android.com/training/app-links) so the OS verifies that your app owns the target domain. Without verification, any other app can register the same intent filter and intercept the links (see @MASTG-KNOW-0019).

## Enable autoVerify on the Intent Filter

Set [`android:autoVerify="true"`](https://developer.android.com/training/app-links/verify-android-applinks) on every `<intent-filter>` that declares an `http`/`https` deep link. This tells Android to confirm the domain association before routing matching links to your app.

```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https" android:host="www.example.com" />
</intent-filter>
```

## Publish a Valid Digital Asset Links File

Host a [Digital Asset Links](https://developers.google.com/digital-asset-links/v1/getting-started) file at `https://<domain>/.well-known/assetlinks.json` that lists your app's package name and signing certificate fingerprint. The file must be:

- Served over HTTPS, without redirects (a redirect from `http` to `https` or `example.com` to `www.example.com` causes [verification to fail](https://developer.android.com/training/app-links/verify-android-applinks#fix-errors)).
- Valid JSON that includes the target app's package.
- Present on **every** host declared in the intent filters, including each subdomain, and at the root domain when a wildcard host is used.

## Verify the Association

App Links are only protective once verification succeeds. Confirm the state on a device with `adb shell pm get-app-links <package>` and re-trigger verification as needed. See @MASTG-TECH-0174 for the full verification workflow.

!!! warning
    Before Android 12 (API level 31), a single non-verifiable link (a missing `autoVerify`, an invalid Digital Asset Links file, or a custom URL scheme in a verified intent filter) can cause Android to skip verification for **all** of the app's App Links. Keep verified intent filters free of unverifiable entries.

## Avoid Relying on Custom URL Schemes for Sensitive Flows

Custom URL schemes (for example, `myapp://`) are never verified by the OS and can be claimed by any app. Prefer verified App Links for any link that triggers a sensitive action or carries sensitive data. If you must accept a custom URL scheme, validate its input as untrusted (see @MASTG-BEST-0071).
