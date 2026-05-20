---
platform: android
title: Uses of Insecure PendingIntent
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
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

The rule identifies **4 findings** where `PendingIntent` creation APIs are used. This includes both insecure and secure implementations.

{{ output.txt }}

## Evaluation

Review each of the reported instances:

- **Lines 26-31**: FAIL - Uses `PendingIntent.getActivity()` with an implicit intent (`ACTION_VIEW`) and `FLAG_UPDATE_CURRENT` without `FLAG_IMMUTABLE`. A malicious app could intercept this PendingIntent and modify its target.

- **Lines 36-41**: FAIL - Uses `PendingIntent.getService()` with `FLAG_MUTABLE` explicitly set. Even though the intent is explicit, the mutable flag allows modification of unfilled fields.

- **Lines 46-51**: FAIL - Uses `PendingIntent.getBroadcast()` with an implicit intent (custom action) and no flags. On API < 31, the absence of flags results in implicit mutability.

- **Lines 57-62**: PASS - Uses `PendingIntent.getActivity()` with `FLAG_IMMUTABLE` and an explicit intent specifying both class and package. This is secure.

The test fails for the first three instances because they either lack `FLAG_IMMUTABLE` or use implicit intents that could be hijacked.
