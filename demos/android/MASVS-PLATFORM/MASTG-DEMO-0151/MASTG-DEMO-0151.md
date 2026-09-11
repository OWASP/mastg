---
platform: android
title: Deep Link Intent Filter Missing android:autoVerify with semgrep
id: MASTG-DEMO-0151
code: [kotlin]
test: MASTG-TEST-0393
kind: fail
---

## Sample

The app handles the `https://deeplink.example.com` App Link in `DeepLinkActivity` and performs a sensitive action (a transfer) when the link is opened. The `amount` parameter is validated correctly (it must be a positive number within an allowed range), so this demo does **not** depend on an input validation flaw.

The weakness is that the App Link is declared **without** `android:autoVerify="true"`, so Android never verifies that the app owns the `deeplink.example.com` domain. Another app can register the same domain to hijack the link, or invoke this exported handler directly, to reach the sensitive action.

{{ MastgTest.kt }}

The App Link is declared in the manifest without `android:autoVerify="true"`:

{{ AndroidManifest.xml # AndroidManifest_reversed.xml }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed manifest.

{{ ../../../../rules/mastg-android-deeplink-autoverify-missing.yml }}

{{ run.sh }}

## Observation

The rule flagged the `http` and `https` `<data>` declarations in the `DeepLinkActivity` `<intent-filter>`, which is missing the `android:autoVerify="true"` attribute.

{{ output.txt }}

## Evaluation

The test case fails because the app declares an `http`/`https` App Link without enabling verification. Without `android:autoVerify="true"`, Android does not confirm that the app owns the `deeplink.example.com` domain, so the app is not its exclusive, verified handler.

Note that the handler validates its input correctly — the issue is not the parameter handling, but that the link itself is unverified. Because the domain is not verified, a malicious app can register the same `https://deeplink.example.com` intent filter and intercept the deep link (and any sensitive data it carries) or present spoofed content. Since the handler is also exported, any app on the device can invoke it directly to trigger the sensitive action. Use @MASTG-TOOL-0004 to confirm the action is reachable:

```bash
adb shell am start -n org.owasp.mastestapp/.DeepLinkActivity -a android.intent.action.VIEW -d "https://deeplink.example.com/transfer?amount=100"
```

After triggering the deep link, tap **Start** in the app to process it. The result (`Transferred 100 units to the linked account`) confirms that the sensitive action is reachable through the unverified App Link.
