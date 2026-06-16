---
platform: android
title: Uses of Insecure PendingIntent
id: MASTG-DEMO-0147
code: [kotlin]
test: MASTG-TEST-0381
---

## Sample

This sample demonstrates insecure uses of `PendingIntent` in Android, including mutable PendingIntents and implicit base intents that could be vulnerable to hijacking by malicious applications.

The code shows four different scenarios:

1. **Mutable PendingIntent with implicit intent**: Creates a `PendingIntent` using `FLAG_UPDATE_CURRENT` without `FLAG_IMMUTABLE`, combined with an implicit intent (only specifying `ACTION_VIEW`). This is the most dangerous combination as it allows a malicious app to intercept and modify the intent.

2. **Explicit FLAG_MUTABLE**: Creates a `PendingIntent` with `FLAG_MUTABLE` explicitly set. While the base intent is explicit (targeting a specific class), the mutable flag allows modification of intent fields.

3. **Broadcast with implicit intent and no flags**: Creates a broadcast `PendingIntent` with an implicit intent (custom action string) and no flags. On API levels below 31, this defaults to mutable.

4. **Secure PendingIntent (PASS)**: Creates a `PendingIntent` with `FLAG_IMMUTABLE` and an explicit intent that specifies both the target class and package. This is the recommended secure approach.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Run the @MASTG-TOOL-0110 rule against the reversed Java code to identify all PendingIntent creation calls.

{{ ../../../../rules/mastg-android-pendingintent-mutable.yml }}

{{ run.sh }}

## Observation

The rule identifies **3 findings** where `PendingIntent` creation APIs include insecure implementations.

{{ output.txt }}

## Evaluation

Review each of the reported instances:

- **Line 25**: FAIL - Uses `PendingIntent.getActivity()` with an implicit intent (`ACTION_VIEW`) and `FLAG_UPDATE_CURRENT` without `FLAG_IMMUTABLE`. A malicious app could intercept this PendingIntent and modify its target.

- **Line 28**: FAIL - Uses `PendingIntent.getService()` with `FLAG_MUTABLE` explicitly set. Even though the intent is explicit, the mutable flag allows modification of unfilled fields.

- **Line 31**: FAIL - Uses `PendingIntent.getBroadcast()` with an implicit intent (custom action) and no flags. On API < 31, the absence of flags results in implicit mutability.

The test fails for the three instances because they either lack `FLAG_IMMUTABLE` or use implicit intents that could be hijacked.

### Confirm the Exposure

The sample creates `PendingIntent` instances using mutable or non-immutable flags. If exposed to external applications, they may become exploitable when shared through notifications, widgets, shortcuts, or IPC mechanisms.

For the `getActivity()` instance, the base intent uses `Intent.ACTION_VIEW` without specifying a target component. If the `PendingIntent` is exposed, an attacker-controlled application capable of handling the same action could be selected as the destination when the intent is resolved.

For the `getService()` instance, `FLAG_MUTABLE` is explicitly specified. If an attacker obtains a reference to the `PendingIntent`, mutable intent fields such as extras, actions, or data URIs may be modified before the intent is delivered to the target service.

For the `getBroadcast()` instance, the broadcast uses an implicit action and does not specify `FLAG_IMMUTABLE`. An attacker-controlled application could register a receiver for the same action and potentially receive or influence the broadcast when the `PendingIntent` is sent.
