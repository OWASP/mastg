---
title: References to Insecure PendingIntent Creation
platform: android
id: MASTG-TEST-0381
type: [static]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0063]
profiles: [L1, L2]
---

## Overview

This test checks for references to [`PendingIntent`](https://developer.android.com/reference/android/app/PendingIntent) creation APIs to identify potentially insecure implementations. A `PendingIntent` wraps an `Intent` that will be executed later on behalf of the app's identity and permissions, making it critical to configure them securely.

`PendingIntent` objects can be obtained through these API methods:

- `PendingIntent.getActivity()`
- `PendingIntent.getActivities()`
- `PendingIntent.getService()`
- `PendingIntent.getForegroundService()`
- `PendingIntent.getBroadcast()`

The primary security concerns when using `PendingIntent` are:

- **Mutability**: A mutable `PendingIntent` allows the receiving app to modify the base intent's unfilled fields (action, data, categories, extras, etc.). This can enable malicious apps to redirect the intent to unintended components or inject malicious data. Use [`PendingIntent.FLAG_IMMUTABLE`](https://developer.android.com/reference/android/app/PendingIntent#FLAG_IMMUTABLE) to prevent modification of the base intent. Note that prior to Android 12 (API level 31), `PendingIntent` objects were mutable by default, while since Android 12 (API level 31), the mutability of each `PendingIntent` object [must be specified](https://developer.android.com/guide/components/intents-filters#DeclareMutabilityPendingIntent "must be specified") using either `FLAG_MUTABLE` or the `FLAG_IMMUTABLE` flag.

- **Implicit Intents**: Using an implicit base intent (without explicitly specifying the target component class) can allow malicious apps to intercept the `PendingIntent` and redirect its execution. An intent should use `setClass()`, `setClassName()`, or `setComponent()` to specify the target component explicitly.

For more details on `PendingIntent` security, refer to @MASTG-KNOW-0024 and the [Android security documentation on pending intents](https://developer.android.com/topic/security/risks/pending-intent).

## Steps

1. Run a static analysis tool (@MASTG-TECH-0014) to identify all usages of:
    - `PendingIntent.getActivity()`
    - `PendingIntent.getActivities()`
    - `PendingIntent.getService()`
    - `PendingIntent.getForegroundService()`
    - `PendingIntent.getBroadcast()`

2. For each identified usage, check:
    - The flags parameter for the presence of `FLAG_IMMUTABLE` or `FLAG_MUTABLE`.
    - The base intent construction to determine if it is explicit (specifies target component) or implicit.

## Observation

The output should contain a list of locations where `PendingIntent` creation APIs are used, along with the flags passed to the API (if identifiable) and whether the base intent appears to be explicit or implicit.

## Evaluation

The test case fails if any of the following conditions are met:

- A `PendingIntent` is created without `FLAG_IMMUTABLE` when the app's `minSdkVersion` is below 31, unless there is a specific need for mutability that is properly justified and the app takes other precautions.
- A `PendingIntent` is created with `FLAG_MUTABLE` without a valid use case requiring mutability (e.g., inline reply actions).
- The base intent is implicit (does not specify the target component using `setClass()`, `setClassName()`, or `setComponent()`), allowing potential hijacking by malicious apps.
