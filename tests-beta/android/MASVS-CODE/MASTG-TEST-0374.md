---
title: References to Implicit Intents Carrying Sensitive Extras
platform: android
id: MASTG-TEST-0374
type: [static, code, manual]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0056]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

An [implicit intent](https://developer.android.com/guide/components/intents-filters) is an `Intent` that does not name a concrete target component. Instead, it declares an action, and optionally data or categories, and Android resolves it to an installed component with a matching `<intent-filter>`. See @MASTG-KNOW-0025 for background on explicit and implicit intents and intent resolution.

Android apps commonly use implicit intents with extras when they intentionally delegate data to another app selected by the system or the user. Typical legitimate uses include sharing user-selected content with `ACTION_SEND`, attaching metadata for an external viewer, or launching a chooser when the user decides which app receives the data.

The issue appears when the app attaches sensitive or security-relevant extras to an implicit intent without constraining the recipient. During intent resolution, any installed app with a matching `<intent-filter>` can become the selected recipient and receive the full extras `Bundle`. This can disclose credentials, session tokens, one-time codes, personal data, account identifiers, or internal state to an untrusted app. Depending on the data, the impact can include privacy leakage, session compromise, account takeover, or unauthorized use of backend APIs.

This test checks whether the app creates and dispatches implicit intents that carry sensitive extras. Relevant patterns include creating an `Intent` with an action, adding extras with `putExtra`, `putExtras`, `replaceExtras`, or a `Bundle`, and dispatching it through APIs such as `startActivity`, `startActivityForResult`, `ActivityResultLauncher.launch`, `startService`, `bindService`, `sendBroadcast`, or related APIs.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain `Intent` creation and dispatch sites where extras are added or propagated. For each reported dispatch, the output should include, when available:

- The code location or caller.
- The `Intent` creation pattern.
- The action, data, type, and categories.
- The extra keys and values, or the source of the extras when values are not statically visible.
- The dispatch API, such as `startActivity`, `startActivityForResult`, `ActivityResultLauncher.launch`, `startService`, `bindService`, or `sendBroadcast`.
- Any target-defining calls or dispatch restrictions visible before dispatch, such as `setPackage`, `setClass`, `setClassName`, `setComponent`, an explicit `Intent(context, Class)` constructor, or a broadcast receiver permission.

## Evaluation

The test case fails if an implicit intent carries sensitive or security-relevant extras and another app can declare or register a matching component to receive them.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023:

- Check whether the intent has an explicit component or package.
- Check whether another app can declare or register a matching `<intent-filter>` for the action, data, and categories.
- For broadcasts, check whether the sender requires a permission that prevents untrusted receivers from receiving it.
- Check whether the extras contain credentials, tokens, session identifiers, one-time codes, personal data, account identifiers, internal commands, or other security-relevant data.
- Check whether the dispatch is an intentional user-selected share/open flow, such as `ACTION_SEND` or a chooser.
