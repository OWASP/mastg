---
title: Verifying App Link Website Association
platform: android
---

Even when a deep link `<intent-filter>` declares `android:autoVerify="true"`, the link is only treated as an [App Link](https://developer.android.com/training/app-links) once Android confirms the website association against the domain's [Digital Asset Links file](https://developers.google.com/digital-asset-links/v1/getting-started). Use this technique to check whether that verification actually succeeded and to identify why it might not have. For background on the verification process and the Digital Asset Links file requirements, see @MASTG-KNOW-0019.

## Using the App Link Verification Tester

Use the [Android "App Link Verification" Tester](https://github.com/inesmartins/Android-App-Link-Verification-Tester) to list deep links and check the verification status of all app links directly from an APK:

```bash
# list all deep links or only app links
python3 deeplink_analyser.py -op list-all -apk ~/Downloads/example.apk

# check the Digital Asset Links for all app links
python3 deeplink_analyser.py -op verify-applinks -apk ~/Downloads/example.apk
```

## Using adb (Android 12 / API level 31 and higher)

Use @MASTG-TOOL-0004 to review the verification state recorded on the device:

```bash
adb shell pm get-app-links com.example.package

com.example.package:
    ID: 01234567-89ab-cdef-0123-456789abcdef
    Signatures: [***]
    Domain verification state:
      example.com: verified
      sub.example.com: legacy_failure
      example.net: verified
      example.org: 1026
```

A state of `verified` confirms the association. Any other value (for example, `legacy_failure` or a numeric error code) means the domain is not verified, so the corresponding links are not handled as App Links. The same information appears in the output of `adb shell dumpsys package com.example.package`.

You can also [invoke domain verification manually](https://developer.android.com/training/app-links/verify-android-applinks#support-updated-domain-verification), [reset the verification state](https://developer.android.com/training/app-links/verify-android-applinks#reset-state), and [review the results](https://developer.android.com/training/app-links/verify-android-applinks#review-results) to test the logic regardless of whether the app targets Android 12.

## Common Reasons Verification Fails

When a domain is not verified, inspect the [Digital Asset Links file](https://developers.google.com/digital-asset-links/v1/getting-started) and the hosting setup for these common causes (see also the [Android documentation on fixing errors](https://developer.android.com/training/app-links/verify-android-applinks#fix-errors)):

- **Missing file**: there is no file at `https://<host>/.well-known/assetlinks.json` (also queryable via `https://digitalassetlinks.googleapis.com/v1/statements:list?source.web.site=<host>`).
- **Served over HTTP** instead of HTTPS.
- **Invalid file**: the JSON is malformed or does not list the target app's package and signing fingerprint.
- **Redirects**: the server redirects the request (for example, `http` to `https` or `example.com` to `www.example.com`).
- **Subdomains**: each declared host needs its own file; a file on `www.example.com` does not cover `mobile.example.com`.
- **Wildcards**: a wildcard host such as `*.example.com` is verified against the file at the root domain `https://example.com/.well-known/assetlinks.json`.
