---
title: Missing Validation of Data Returned from Inter-App Intent Results
platform: android
id: MASTG-TEST-0375
type: [dynamic, hooks, manual]
weakness: MASWE-0083
best-practices: [MASTG-BEST-0057]
knowledge: [MASTG-KNOW-0025, MASTG-KNOW-0138]
profiles: [L1, L2]
---

## Overview

Apps commonly use the activity result APIs to request data from another app — for example, selecting a file, opening a document, or importing content. This applies to both **implicit intents** (where Android resolves the target from installed components matching an `<intent-filter>`) and **explicit intents** that target a specific external app by package name or component (for example, a dedicated "Attach file from Dropbox" button that hardcodes the Dropbox package). See @MASTG-KNOW-0025 for background on explicit and implicit intents and intent resolution.

In both cases, the **responding app fully controls the result** returned to the caller, including values such as `Intent.getData()`, `ClipData`, extras, and provider metadata returned through `ContentResolver` queries such as `OpenableColumns.DISPLAY_NAME`. The fact that an explicit intent was used does not make the responding app inherently trusted — a legitimate app can still return malformed, unexpected, or malicious data, and a compromised or malicious app installed under the targeted package name can return arbitrary values.

The issue appears when the caller treats the returned data as trusted without validation. A responder can return unexpected URI schemes, provider-controlled metadata, filenames with path separators, or values that influence app behavior. If the caller uses those values without validation, they can affect file handling, content parsing, storage, navigation, backend requests, authorization decisions, account selection, transaction flows, or other security-relevant logic.

This test dynamically checks whether data returned from any inter-app intent result — implicit or explicit — reaches security-relevant operations without validation or sanitization. Relevant API calls include the APIs used to launch the request (`startActivityForResult`, `ActivityResultLauncher.launch`), receive the result (`onActivityResult`, `ActivityResultCallback.onActivityResult`), read returned data (`Intent.getData`, `Intent.getClipData`, `Intent.getExtras`, `ContentResolver.query`, `ContentResolver.openInputStream`), and process the returned values in security-relevant code.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger flows that request data from another app through an intent result. Cover both:
    - **Implicit intent flows**: file pickers, document selectors, share sheets, or any flow where Android presents a chooser.
    - **Explicit intent flows**: dedicated integrations that target a specific external app by package name or component (for example, "Attach from Dropbox", "Import from Drive", or any similar feature that names the external app).

## Observation

The output should contain runtime traces of intent result handling flows. The output should include, when available:

- The request intent details, such as action, data, type, categories, extras, target package or component (if explicit), and launch API.
- The result callback or handler, such as `onActivityResult` or `ActivityResultCallback.onActivityResult`.
- Returned data read by the app, such as `Intent.getData()`, `ClipData`, extras, or `ContentProvider` metadata.
- APIs used to read returned data, such as `ContentResolver.query` or `ContentResolver.openInputStream`.
- App operations reached after reading the returned data, including arguments and hook backtraces.

## Evaluation

The test case fails if data returned from an external intent result — whether the originating intent was implicit or explicit — reaches a security-relevant operation without validation or sanitization.

**Further Validation Required:**

Using the hook backtraces, inspect each reported code location using @MASTG-TECH-0023:

- Check whether the returned data comes from `Intent.getData()`, `ClipData`, extras, or `ContentProvider` metadata.
- Check whether the returned data is controlled by an external responder (regardless of whether the intent that triggered the flow was implicit or explicit).
- Check whether the returned data affects file handling, content parsing, storage, navigation, backend requests, authorization decisions, account selection, transaction flows, or other security-relevant logic.
- Check whether the app validates the returned data before use.
