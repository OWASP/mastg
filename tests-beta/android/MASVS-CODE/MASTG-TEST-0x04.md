---
title: Runtime Use of Implicit Intents for Arbitrary Code Execution via Malicious ContentProvider
platform: android
id: MASTG-TEST-0x02
type: [dynamic]
weakness: MASWE-0083
best-practices: [MASTG-BEST-0x14, MASTG-BEST-0x15]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

If an app uses an implicit intent to request a file or library and processes the returned `content://` URI by querying the providing app's `ContentProvider` for a filename, an attacker can exploit this in two ways: first, by returning a path-traversal filename (e.g., `../lib_config.json` or `../lib-main/lib.so`) via `ContentProvider.query()`, and second, by serving malicious content via `ContentProvider.openFile()`. The victim app writes the attacker-controlled content to its internal storage using the unsanitized filename, potentially overwriting legitimate files such as configuration files or native libraries. If the overwritten file is subsequently loaded via `System.load()`, the attacker achieves arbitrary code execution within the victim app's process and with its full permissions.

## Steps

1. Install the vulnerable app and an attacker app that declares a matching intent-filter and an exported `ContentProvider` on the device using @MASTG-TECH-0004.
2. Verify the legitimate internal file exists before the attack.
3. Launch the vulnerable app and trigger the implicit intent by interacting with the Start button.
4. When the system resolver dialog appears, select the attacker app.
5. Monitor logcat output filtered by `MASTG-DEMO` and `ATTACKER` tags and observe the output the screen.
6. Verify the internal file was overwritten with attacker-controlled content.

## Observation

The output should contain evidence that the attacker's `ContentProvider.query()` returned a path-traversal filename and `ContentProvider.openFile()` served malicious content. The victim app should have written the attacker-controlled content to its internal storage using the unsanitized filename, overwriting a legitimate file.

## Evaluation

The test case fails if the app sends an implicit intent that can be intercepted by a third-party app, queries an untrusted `ContentProvider` for a filename without sanitizing path-traversal sequences (e.g., `../`), and writes attacker-controlled content to internal storage using the unsanitized filename. This enables an attacker to overwrite arbitrary files within the victim's internal storage, potentially achieving code execution if a native library or executable file is overwritten.
