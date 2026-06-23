---
title: Implicit Intents Used for Internal App Communication
platform: android
id: MASTG-TEST-0372
type: [static, code, manual]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0056]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

An [implicit intent](https://developer.android.com/guide/components/intents-filters) is an `Intent` that does not name a concrete target component. Instead, it declares an action, and optionally data or categories, and Android resolves it to an installed component with a matching `<intent-filter>`. See @MASTG-KNOW-0025 for background on explicit and implicit intents and intent resolution.

Android apps commonly use implicit intents when they intentionally delegate an action to another app selected by the system or the user. Typical legitimate uses include opening a web page or map location with `ACTION_VIEW`, sharing content with `ACTION_SEND`, requesting a file or image with `ACTION_GET_CONTENT`, or launching a chooser when the app does not need to control which external app handles the request.

The issue appears when an app uses the same mechanism for communication that is expected to stay inside the app, such as starting an internal activity or sending an app-specific action to another trusted component. In that case, intent hijacking can occur: a third-party app declares a matching `<intent-filter>` for the same action and becomes a candidate during Android's intent resolution. If the system presents a chooser, the user may select the third-party app; if only one matching handler exists or a default handler has been set, the intent may be delivered without an explicit user decision.

This test checks whether the app creates and dispatches implicit intents for internal communication. Relevant creation and dispatch patterns include `Intent(String)`, `Intent().setAction(...)`, `startActivity`, `startActivityForResult`, `ActivityResultLauncher.launch`, `startService`, `bindService`, `sendBroadcast`, and related APIs that send an `Intent` without naming a concrete recipient.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain `Intent` creation and dispatch sites. For each reported dispatch, the output should include, when available:

- The code location or caller.
- The `Intent` creation pattern.
- The action, data, type, categories, and extras visible at the dispatch site.
- The dispatch API, such as `startActivity`, `startActivityForResult`, `ActivityResultLauncher.launch`, `startService`, `bindService`, or `sendBroadcast`.
- Any target-defining calls or dispatch restrictions visible before dispatch, such as `setPackage`, `setClass`, `setClassName`, `setComponent`, an explicit `Intent(context, Class)` constructor, or a broadcast receiver permission.

## Evaluation

The test case fails if an intent used for app-internal or trusted-component communication is implicit and another app can declare or register a matching component to receive it.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023:

- Check whether the intent has an explicit component or package.
- Check whether another app can declare or register a matching `<intent-filter>` for the action, data, and categories.
- For broadcasts, check whether the sender requires a permission that prevents untrusted receivers from receiving it.
- Check whether the intent is meant for an app component or another trusted app, not a user-selected external app.
