---
platform: android
title: Clearing FLAG_SECURE in an Activity and Dialog with semgrep
id: MASTG-DEMO-0061
code: [kotlin]
test: MASTG-TEST-0291
---

### Sample

This sample demonstrates multiple methods for preventing screenshots of sensitive content in two Android components: an Activity and a standard Dialog. It shows how the `FLAG_SECURE` flag is set for each component and then immediately removed.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed Java code.

{{ ../../../../rules/mastg-android-sensitive-data-in-screenshot.yml }}

{{ run.sh }}

### Observation

The rule has identified several locations in the code file where the app sets or clears the `FLAG_SECURE` window flag.

{{ output.txt }}

### Evaluation

The test fails because even though the app demonstrates correct protection patterns by setting `FLAG_SECURE` for an Activity and a dialog, it subsequently removes protection by clearing/overwriting `FLAG_SECURE` on both the Activity and the dialog.

We can see this in `MastgTest_reversed.java`:

- For the activity:
    - it adds the `FLAG_SECURE` flag via `getWindow().addFlags(8192)` (line 35)
    - sets it again with `getWindow().setFlags(8192, 8192)` (line 36)
    - and then immediately clears it with `getWindow().setFlags(0, 8192)` (line 37)
- For the dialog:
    - it sets `window.setFlags(8192, 8192)` (line 42)
    - and then calls `window2.clearFlags(8192)` (line 46)
