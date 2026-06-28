---
platform: android
title: SafeBrowsing Disabled
id: MASTG-TEST-0399
apis: [WebView, WebSettings, EnableSafeBrowsing, setSafeBrowsingEnabled]
type: [static, config, code]
weakness: MASWE-0071
best-practices: []
knowledge: [MASTG-KNOW-0018]
available_since: 27
profiles: [L1, L2]
---

## Overview

This test checks whether the [SafeBrowsing API](https://developers.google.com/safe-browsing/) is explicitly disabled, either in the AndroidManifest.xml or in the WebView code. Since Android 8.1 (API level 27), WebViews include SafeBrowsing by default, which warns users about URLs that Google has classified as known threats such as phishing or malware sites.

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

Apps can also disable SafeBrowsing at runtime by calling [`WebSettings.setSafeBrowsingEnabled(false)`](https://developer.android.com/reference/android/webkit/WebSettings#setSafeBrowsingEnabled(boolean)) on a WebView instance. This takes precedence over the manifest setting, so even if the manifest enables SafeBrowsing, it can still be disabled in code.

Disabling SafeBrowsing removes an important security layer that protects users from navigating to malicious websites.

See @MASTG-KNOW-0018 for more information on the SafeBrowsing API.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to check the relevant attribute.
4. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain any location where SafeBrowsing is disabled: the `android.webkit.WebView.EnableSafeBrowsing` meta-data set to `false` in the AndroidManifest.xml, or a `WebSettings.setSafeBrowsingEnabled(false)` call in the WebView code.

## Evaluation

The test case fails if the `android.webkit.WebView.EnableSafeBrowsing` meta-data is present with `android:value="false"`, or if SafeBrowsing is disabled in code via `WebSettings.setSafeBrowsingEnabled(false)`. Because the code setting takes precedence over the manifest, a WebView is protected only when SafeBrowsing is neither disabled in the manifest nor in code.
