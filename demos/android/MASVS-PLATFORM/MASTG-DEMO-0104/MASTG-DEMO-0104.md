---
platform: android
title: App Requesting SYSTEM_ALERT_WINDOW Permission
id: MASTG-DEMO-0104
code: [xml]
test: MASTG-TEST-0340
tools: [semgrep]
kind: info
---

## Sample

The manifest below is from an attacker app that requests the `SYSTEM_ALERT_WINDOW` permission, which allows it to draw overlays over other apps.

{{ AndroidManifest.xml # AndroidManifest_reversed.xml # MastgTest.kt }}

You can use this attacker app to demonstrate the vulnerability shown in @MASTG-DEMO-0103: install and run it while the victim app is in the foreground, then activate the overlay. This lets you verify whether the unprotected button in the victim app accepts touch events through the overlay.

Note that the `SYSTEM_ALERT_WINDOW` permission itself isn't a vulnerability in the app that declares it. It's a legitimate Android feature used by apps such as screen overlay tools, chat heads, or accessibility services. However, its presence means the app can display overlays over other apps, which can be used to conduct overlay attacks against victim apps that don't implement proper protections.

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed manifest to check for the `SYSTEM_ALERT_WINDOW` permission.

{{ ../../../../rules/mastg-android-system-alert-window.yml }}

{{ run.sh }}

## Observation

The rule detected the `SYSTEM_ALERT_WINDOW` permission in the manifest:

{{ output.txt }}

## Evaluation

The finding confirms that this app declares the `SYSTEM_ALERT_WINDOW` permission. This isn't a vulnerability in this app itself; it shows that the app has the capability to draw overlays over other apps. The actual vulnerability lies in victim apps (like the one in @MASTG-DEMO-0103) that don't implement overlay protections on sensitive UI elements.

When assessing an app for overlay attack vulnerabilities, the threat model should include apps that can request `SYSTEM_ALERT_WINDOW` to create overlays.
