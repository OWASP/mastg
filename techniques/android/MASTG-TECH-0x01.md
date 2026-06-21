---
title: Deep Link Testing
platform: android
---

This technique groups common tasks for assessing deep links on Android: detecting usage, validating website association, monitoring its resolution, and invoking links.

## Verify Usage of Deep Links

Deep link entry points can be discovered by examining intent filters in the `AndroidManifest.xml` and reviewing the code to understand how incoming deep link data is processed and validated.

### Inspection of the AndroidManifest File

Extract and review intent filters that declare VIEW/BROWSABLE handlers. Use @MASTG-TOOL-0124, then select `intent-filter` with `android.intent.action.VIEW"` and `android.intent.category.BROWSABLE`.

```bash
aapt2 dump xmltree app.apk AndroidManifest.xml 
```

### Using Dumpsys

Use adb to run the following command that will show all schemes:

`adb shell dumpsys package com.example.package`

### Using the Android App Link Verification Tester

Use the Android @MASTG-TOOL-0x01 Tester to list all deep links (list-all) or only app links (list-applinks):

```bash
 python3 deeplink_analyser.py -op list-all -apk example.apk

org.owasp.mastestapp.MainActivity

vulnerable-app://deeplink
```

## Verify Domain Association Setup for App Links

Android App Links must be verified against a website's Digital Asset Links to ensure only the legitimate app handles links for that domain.

### Verify App Links state

```bash
# Trigger verification
adb shell pm verify-app-links com.example.app

# Inspect current state
adb shell pm get-app-links com.example.app

# Reset selection (optional) and re-verify
adb shell pm reset-app-links com.example.app
adb shell pm verify-app-links com.example.app
```

Preferential handling and verification status per host should be listed. Unverified hosts indicate missing or incorrect associations.

### Validate assetlinks.json

Fetch the site statements and verify package name and sha256_cert_fingerprints match the app's signing cert

```bash
curl -s https://example.com/.well-known/assetlinks.json | jq
```

Check for invalid Digital Asset Links files served via HTTPS. For example:

- The file contains invalid JSON.
- The file doesn't include the target app's package.
- If an intent-filter lists multiple hosts with different subdomains, there must be a valid Digital Asset Links file on each domain.

@MASTG-TOOL-0x01 can be also used get the verification status for all app links (verify-applinks).

## Monitoring Deep Links

When using deep links, monitor how the system resolves and dispatches the Intent.

### Using Logcat

Use @MASTG-TOOL-0004 to tail logs or @MASTG-TOOL-0112 for app-scoped output:

```bash
# System components that log VIEW intent dispatch
adb logcat -s ActivityTaskManager IntentResolver

# App-scoped logs with pidcat (replace with your package)
pidcat com.example.app
```

### Dumpsys insights

`dumpsys` can reveal resolution state and preferred handlers:

```bash
adb shell dumpsys package domain-preferred-apps
```

## Invoking Deep Links

Deep links can be triggered without user interaction to validate routing logic and security controls. You can invoke both custom URL schemes and App Links from the command line and from assessment tooling.

### Using Activity Manager (am)

Use @MASTG-TOOL-0004 to start an Activity handling a deep link Intent:

```bash
# Custom scheme
adb shell am start -W \
  -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -d "vulnerable-app://deeplink?url=https://attacker.example/"

# App Link (https)
adb shell am start -W \
  -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -d "https://example.com/deeplink/path?foo=bar"
```

```bash
adb shell am start -W \
  -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -d "https://example.com/deeplink/path?foo=bar" \
  --es "utm_source" "google" \
  --ei "user_id" 12345 \
  --ez "is_promo" true
```

Adding extras:

- Add extras with --es key value, --ei, --ez, etc.

### Using drozer

You can also craft and fire Intents with @MASTG-TOOL-0015:

```bash
dz> run app.activity.start \
  --action android.intent.action.VIEW \
  --category android.intent.category.BROWSABLE \
  --data-uri "https://example.com/deeplink/path?foo=bar"
```

This is useful to exercise exported Activities and intent filters from a malicious-app perspective.
