---
platform: android
title: Use of Unverified Custom URL Schemes with semgrep
id: MASTG-DEMO-0x03
code: [kotlin]
test: MASTG-TEST-0x03
status: new
---

## Sample

The following is a sample `AndroidManifest.xml` snippet that defines a deep link intent filter using custom URL schemes which can be registered by multiple apps, allowing interception or spoofing.

{{ AndroidManifest_reversed.xml # AndroidManifest.xml }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed AndroidManifest file.

{{ ../../../../rules/mastg-android-custom-deeplink-scheme.yml }}

{{ run.sh }}

## Observation

The rule has identified one location in the manifest file where the app has set the custom URL schemes.

{{ output.txt }}

## Evaluation

The test fails because a sensitive action (toggling app state) is performed through an unverified custom URL scheme with no authentication/authorization or user confirmation. Any app can register the same scheme/host to intercept or spoof these links. Trigger the deep link using @MASTG-TOOL-0004:

```bash
adb shell am start -W -n org.owasp.mastestapp/.DeepLinkActivity -a android.intent.action.VIEW -d "mastestapp://toggle?state=on"
```
