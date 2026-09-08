---
platform: android
title: SafeBrowsing Disabled Detection with semgrep
id: MASTG-DEMO-0156
code: [xml, kotlin]
test: MASTG-TEST-0399
---

## Sample

The following sample explicitly enables SafeBrowsing for WebViews in the AndroidManifest.xml by setting the `android.webkit.WebView.EnableSafeBrowsing` meta-data to `true`. However, in the WebView code, SafeBrowsing is disabled via `WebSettings.setSafeBrowsingEnabled(false)`, which takes precedence over the manifest setting. This demonstrates that the manifest setting alone is not sufficient to determine whether SafeBrowsing is enabled.

{{ AndroidManifest.xml # AndroidManifest_reversed.xml # MastgTestWebView.kt # MastgTestWebView_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reverse-engineered manifest and code. The rule flags SafeBrowsing being disabled, either via the `EnableSafeBrowsing` meta-data set to `false` in the AndroidManifest.xml or via a `WebSettings.setSafeBrowsingEnabled(false)` call in the WebView code.

{{ ../../../../rules/mastg-android-webview-safebrowsing.yml }}

{{ run.sh }}

## Observation

The output shows that SafeBrowsing is disabled in the WebView code via `setSafeBrowsingEnabled(false)`.

{{ output.txt }}

## Evaluation

The test case fails because the WebView code disables SafeBrowsing via `WebSettings.setSafeBrowsingEnabled(false)` (reported on line 26 of the reverse-engineered code). Even though the `android.webkit.WebView.EnableSafeBrowsing` meta-data is set to `android:value="true"` in the manifest, the code setting takes precedence, so SafeBrowsing is disabled for all WebViews in the app, removing an important security layer that protects users from known malicious URLs. This is why the WebView code must be checked in addition to the manifest.

If you remove the call to `WebSettings.setSafeBrowsingEnabled(false)`, SafeBrowsing is enabled by default and the runtime logcat shows it blocking known malicious URLs, as shown below:

```bash
2026-06-28 10:53:56.746 10662-10662 SafeBrowsing            org.owasp.mastestapp                 D  Blocked URL, chrome://safe-browsing/match?type=phishing, threatType, 2
2026-06-28 10:53:56.748 10662-10662 SafeBrowsing            org.owasp.mastestapp                 D  Blocked URL, chrome://safe-browsing/match?type=phishing, threatType, 2
```
