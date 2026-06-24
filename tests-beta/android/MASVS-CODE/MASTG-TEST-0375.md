---
title: Missing Validation of Data Returned from Implicit Intents
platform: android
id: MASTG-TEST-0375
type: [dynamic, hooks, manual]
weakness: MASWE-0083
best-practices: [MASTG-BEST-0057]
knowledge: [MASTG-KNOW-0025, MASTG-KNOW-0138]
profiles: [L1, L2]
---

## Overview

An [implicit intent](https://developer.android.com/guide/components/intents-filters) is an `Intent` that does not name a concrete target component. Instead, it declares an action, and optionally data or categories, and Android resolves it to an installed component with a matching `<intent-filter>`. See @MASTG-KNOW-0025 for background on explicit and implicit intents and intent resolution.

Apps commonly use implicit intents and activity result APIs to request data from another app, such as selecting a file, opening a document, or importing content. The selected responder controls the result returned to the caller, including values such as `Intent.getData()`, `ClipData`, extras, and provider metadata returned through `ContentResolver` queries, such as `OpenableColumns.DISPLAY_NAME`.

The issue appears when the app treats the returned data as trusted. A responder can return unexpected URI schemes, provider-controlled metadata, filenames with path separators, or values that influence app behavior. If the caller uses those values without validation, they can affect file handling, content parsing, storage, navigation, backend requests, authorization decisions, account selection, transaction flows, or other security-relevant logic.

This test dynamically checks whether data returned from an implicit intent result reaches security-relevant operations without validation or sanitization. Relevant API calls include the APIs used to launch the request (`startActivityForResult`, `ActivityResultLauncher.launch`), receive the result (`onActivityResult`, `ActivityResultCallback.onActivityResult`), read returned data (`Intent.getData`, `Intent.getClipData`, `Intent.getExtras`, `ContentResolver.query`, `ContentResolver.openInputStream`), and process the returned values in security-relevant code.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger flows that request data from another app through an implicit intent.

## Observation

The output should contain runtime traces of intent result handling flows. The output should include, when available:

- The request intent details, such as action, data, type, categories, extras, and launch API.
- The result callback or handler, such as `onActivityResult` or `ActivityResultCallback.onActivityResult`.
- Returned data read by the app, such as `Intent.getData()`, `ClipData`, extras, or `ContentProvider` metadata.
- APIs used to read returned data, such as `ContentResolver.query` or `ContentResolver.openInputStream`.
- App operations reached after reading the returned data, including arguments and hook backtraces.

## Evaluation

The test case fails if data returned from an external intent result reaches a security-relevant operation without validation or sanitization.

**Further Validation Required:**

Using the hook backtraces, inspect each reported code location using @MASTG-TECH-0023:

- Check whether the returned data comes from `Intent.getData()`, `ClipData`, extras, or `ContentProvider` metadata.
- Check whether the returned data is controlled by an external responder.
- Check whether the returned data affects file handling, content parsing, storage, navigation, backend requests, authorization decisions, account selection, transaction flows, or other security-relevant logic.
- Check whether the app validates the returned data before use.
