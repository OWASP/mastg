---
platform: android
title: Sensitive Data Exposed via Implicit Intents
id: MASTG-TEST-0x02
type: [dynamic]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0x14]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

When an Android app sends an implicit intent, the system resolves the intent across every installed app. Any app that declares a matching `<intent-filter>` can receive the intent, including its extras. If the originating app attaches sensitive data (for example, authentication tokens, user credentials, API keys, personal identifiers) to such an intent, that data is exposed to whatever app the system or the user routes the intent to. Even if the developer expects only their own app to handle the intent, a malicious app that registers the same action receives the extras unchanged. The only way to prevent this exposure is to restrict the target of the intent so that it cannot leave the sending app — for example, by setting the package or the component explicitly.

## Steps

1. Install the vulnerable app on the device.
2. Install an attacker app that declares an `<intent-filter>` matching the action used by the vulnerable app.
3. Launch the vulnerable app and trigger the code path that sends the implicit intent carrying sensitive data.

## Observation

The output should contain evidence that the attacker app received the implicit intent's extras (such as tokens, credentials, or API keys) without any authorization check, confirming that the sensitive payload was disclosed to an arbitrary app on the device.

## Evaluation

The test case fails if the app sends an implicit intent carrying sensitive data (for example, tokens, credentials, or API keys) without restricting the target component.
