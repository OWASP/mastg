---
platform: android
title: Dynamic Detection of Arbitrary Code Execution Using Implicit Intent Hijacking
id: MASTG-DEMO-0x04
code: [kotlin]
test: MASTG-TEST-0x04
---

## Sample

The demo consists of a vulnerable application that relies on an implicit intent (`android.intent.action.GET_CONTENT`) to process content from a returned URI, and a second application that hijacks this intent to enable arbitrary code execution using a malicious native library served through a `ContentProvider`.

## Vulnerable App

The vulnerable app uses `startActivityForResult` with an implicit intent to request a file. When the result is returned, the app copies the content referenced by the URI into its internal library directory and loads it. This allows an attacker-controlled `ContentProvider` to supply a malicious shared library.

{{ MastgTest.kt }}

The `LibraryLoaderActivity` handles the returned URI by copying the content to the app's native library directory and loading it via `System.load()`. Since the URI is not validated, an attacker can substitute a malicious `.so` file.

{{ LibraryLoaderActivity.kt }}

## Attacker App

The attacker app registers a high-priority intent filter for `android.intent.action.GET_CONTENT`. When selected, it returns a `content://` URI that resolves to the attacker's `ContentProvider`. The `ContentProvider` uses `query` to set a path-traversal filename (e.g., `../lib-main/lib.so`) and `openFile()` to serve a malicious native library.

{{ attacker/EvilContentActivity.kt # attacker/EvilContentProvider.kt }}

## Steps

1. Prepare a malicious shared library (`fakelib.so`) and place it in the attacker app's data directory.
2. Install the attacker and vulnerable app on a device.
3. On the vulnerable app, click **Start** button to trigger the file selection intent.
4. If a chooser dialog appears, select the attacker app. If the victim app auto-selects via `queryIntentActivities`, the attacker app is chosen automatically due to its high-priority intent filter.

## Observation

The output contains an interception of the `GET_CONTENT` intent and returns a `content://` URI controlled by the attacker. The vulnerable app queries this URI, receiving a filename with a path-traversal component (`../lib-main/lib.so`). It then opens the URI via `ContentProvider.openFile`, which serves the attacker's malicious `fakelib.so`. The victim app copies this file into its library directory and loads it via `System.load`, executing the attacker's code within the victim app's process and with its full permissions.

The app loads the attacker-supplied library `content://com.attacker.evil/malicious_lib`

## Evaluation

The test case fails because:

1. The app uses an implicit intent (`GET_CONTENT`) without restricting the handler.
2. The returned `content://` URI is not validated before use.
3. The `ContentProvider.query` returns a path-traversal filename that overwrites a legitimate library.
4. The `ContentProvider.openFile` serves a malicious native library.
5. The victim app loads the attacker-supplied library via `System.load`, achieving arbitrary code execution in the victim's context.
