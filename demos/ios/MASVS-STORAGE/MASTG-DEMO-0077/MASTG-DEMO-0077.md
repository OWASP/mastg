---
platform: ios
title: Runtime Monitoring of Text Fields Eligible for Keyboard Caching with Frida
id: MASTG-DEMO-0077
code: [swift]
test: MASTG-TEST-0314
---

### Sample

This demo uses the same sample as @MASTG-DEMO-0076.

{{ ../MASTG-DEMO-0076/MastgTest.swift }}

### Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn your app with Frida.
4. Click the **Start** button.
5. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

The script hooks into text input controls at runtime and monitors when they lose focus. For each interaction, it captures the entered text, the input field class, accessibility identifier when available, placeholder text when available, and the relevant input traits such as `autocorrectionType`, `spellCheckingType`, and `isSecureTextEntry`. Based on these values, it reports whether the input is eligible for keyboard caching.

### Observation

{{ output.txt }}

The output contains all text that the user entered in every text input, along with its keyboard-cache eligibility.

### Evaluation

The test fails because the output shows two text fields that are eligible for keyboard caching and contain sensitive information: the user's name and email address.

- The name input has `autocorrectionType` set to `default`, making it eligible for keyboard caching.
- The email input has `autocorrectionType` set to `no` but `spellCheckingType` set to `default`, making it eligible for keyboard caching.

The password input is protected against keyboard caching because `isSecureTextEntry` is set to `true`.
