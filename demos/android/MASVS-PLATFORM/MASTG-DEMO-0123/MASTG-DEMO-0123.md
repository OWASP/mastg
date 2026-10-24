---
platform: android
title: Exfiltration of Private Files via FileProvider URI Grant Oversharing
id: MASTG-DEMO-0123
code: [kotlin]
kind: attack
test: MASTG-TEST-0357
---

## Sample

This demo uses the same victim app as @MASTG-DEMO-0122. The victim's `ShareReportActivity` is an exported activity that accepts a caller-supplied `file_name` parameter, calls `FileProvider.getUriForFile()` with the requested filename, and returns the resulting `content://` URI to the caller via `FLAG_GRANT_READ_URI_PERMISSION`.

Because `file_paths.xml` declares `path="."`, the `FileProvider` will accept any file under `filesDir` — not just the intended `reports/` subdirectory. The attacker app below (`org.owasp.mastestapp.attacker.provider`) exploits this by passing `session_token.txt` as the `file_name` parameter and reading the returned URI content.

{{ MastgTest.kt # AndroidManifest.xml }}

## Steps

1. Install the victim app (@MASTG-DEMO-0122) using @MASTG-TECH-0005 and tap **Start** to populate `filesDir`.
2. Build and install the attacker APK (`MastgTest.kt`, `AndroidManifest.xml`) using @MASTG-TECH-0005.
3. Tap **Start** in the attacker app to launch the attack.
4. Run `run.sh` to capture the exfiltrated content from logcat.

{{ run.sh }}

## Observation

The attacker app displays a dialog showing the exfiltrated content. The same value is captured in logcat by `run.sh`.

{{ output.txt }}

## Evaluation

The test case fails because the attacker app successfully reads `session_token.txt` from the victim app's private storage via a URI grant from `ShareReportActivity`.

The token `sess_7f3a9b1e4d2c8f0a5e6b3c1d9f4a2e7b` visible in the logcat output was written by the victim app to `filesDir/session_token.txt`. The `FileProvider.getUriForFile()` call in `ShareReportActivity` accepts this path because `file_paths.xml` uses `path="."`, making the entire `filesDir` accessible — not just the intended `reports/` subdirectory.

This confirms the security relevance required by the "Further Validation Required" section in @MASTG-TEST-0357. `FileProvider.getUriForFile()` is called with an attacker-controlled `file_name` parameter, derived directly from the intent extras, and the app performs no validation before granting the resulting URI to the caller.
