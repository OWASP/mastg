---
platform: android
title: Deep Link Intent Filter Missing android:autoVerify with semgrep
id: MASTG-DEMO-0x02
code: [kotlin]
test: MASTG-TEST-0x02
status: new
---

## Sample

The following is a sample `AndroidManifest.xml` snippet that defines a deep link intent filter without the `android:autoVerify="true"` attribute.

{{ AndroidManifest_reversed.xml # AndroidManifest.xml }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample manifest.

{{ ../../../../rules/mastg-android-autoverify-missing.yml }}

{{ run.sh }}

## Observation

The rule has identified that the deep link intent filter is missing the `android:autoVerify="true"` attribute.

{{ output.txt }}

## Evaluation

The test fails because the app does not enforce Android App Links verification. Without `android:autoVerify="true"`, Android never verifies domain ownership against the site's `/.well-known/assetlinks.json`, so `https://deeplink.example.com` is treated as an unverified deep link. Any malicious app can register the same host and scheme to intercept or spoof these links, leading to phishing or hijacking attacks.

To demonstrate the impact, the app routes this link to an exported `DeepLinkActivity` that performs a sensitive action (disabling two-factor authentication) with no verification or user confirmation. Trigger the deep link using @MASTG-TOOL-0004:

```bash
adb shell am start -W -n org.owasp.mastestapp/.DeepLinkActivity -a android.intent.action.VIEW -d "https://deeplink.example.com/security?twofa=off"
```

The sensitive setting is changed without any confirmation, showing how any app holding this unverified link can trigger the action.
