---
title: App Link Verification
platform: android
source: https://github.com/inesmartins/Android-App-Link-Verification-Tester/tree/main
---

This is a command-line tool written in Python which helps you verify whether an Android app has properly completed the App Link Verification process for Android App Links.

It supports several modes including listing all registered deep links, listing only App Links, performing full verification checks, building a "proof-of-concept" HTML page and launching it on a device via ADB.

```bash
python3 deeplink_analyser.py -op list-all -apk example.apk
```
