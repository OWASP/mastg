---
platform: android
title: SafeBrowsing Disabled in AndroidManifest
id: MASTG-TEST-0399
apis: [WebView, EnableSafeBrowsing]
type: [static, config]
weakness: MASWE-0071
best-practices: []
available_since: 27
profiles: [L1, L2]
---

## Overview

This test checks whether the [SafeBrowsing API](https://developers.google.com/safe-browsing/v4) is explicitly disabled in the AndroidManifest.xml. Since Android 8.1 (API level 27), WebViews include SafeBrowsing by default, which warns users about URLs that Google has classified as known threats such as phishing or malware sites.

While SafeBrowsing is enabled by default, applications can disable it by setting the `android.webkit.WebView.EnableSafeBrowsing` meta-data to `false` in the manifest:

```xml
<manifest>
    <application>
        <meta-data android:name="android.webkit.WebView.EnableSafeBrowsing"
                   android:value="false" />
        ...
    </application>
</manifest>
```

Disabling SafeBrowsing removes an important security layer that protects users from navigating to malicious websites.

See @MASTG-KNOW-0018 for more information on the SafeBrowsing API.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to check the relevant attribute.

## Observation

The output should indicate whether the SafeBrowsing setting is present in the manifest and its value.

## Evaluation

The test case fails if the `android.webkit.WebView.EnableSafeBrowsing` meta-data is present with `android:value="false"`.

If this meta-data is not present, SafeBrowsing uses its default value of `true`, which is the secure configuration.
