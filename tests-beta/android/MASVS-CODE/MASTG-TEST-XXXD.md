---
title: Runtime Use of Implicit Intents for Arbitrary File Read
platform: android
id: MASTG-TEST-XXXD
type: [dynamic]
weakness: MASWE-0083
profiles: [L1, L2]
---

## Overview

If an app uses an implicit intent to request content from other apps without specifying a target component, a malicious app can register a matching intent-filter and intercept the intent. When the victim app processes the returned URI without validation, the attacker can supply a `file://` URI pointing to the victim's internal storage. If the victim then copies this content to a world-readable location (e.g., external cache), sensitive data such as authentication tokens, API keys, and session credentials stored in SharedPreferences or internal files becomes accessible to the attacker. This is especially critical when the app stores sensitive data in internal storage and relies on implicit intents for inter-component communication.

## Steps

1. Install the vulnerable app and an attacker app that declares a matching intent-filter for the same custom action on the device using @MASTG-TECH-0004.
2. Launch the vulnerable app and trigger the implicit intent by interacting with the relevant UI element.
3. When the system resolver dialog appears, select the attacker app.
4. Monitor logcat output filtered by `MASTG-DEMO` and `ATTACKER` tags.
5. Inspect the victim app's external cache directory for exfiltrated files.

## Observation

The output should contain evidence that the attacker app intercepted the implicit intent and returned a `file://` URI pointing to the victim's internal storage. The victim app should have copied the referenced file to its external cache directory without validating the URI origin or path.

## Evaluation

The test case fails if the app sends an implicit intent that can be intercepted by a third-party app, and processes the returned URI by copying the referenced content to a world-readable location without validating its origin, path, or scheme. This enables an attacker to read arbitrary files from the victim's internal storage.
