---
platform: ios
title: Runtime Monitoring of Text Input Fields Not Hiding Sensitive Data
id: MASTG-DEMO-0113
code: [swift]
test: MASTG-TEST-0347
kind: fail
---

## Sample

This demo uses the same sample as @MASTG-DEMO-0113.

{{ ../MASTG-DEMO-0113/MastgTest.swift }}

## Steps

1. Make sure @MASTG-TOOL-0039 is installed on your machine and the frida-server running on the device.
2. Run `run.sh` to spawn the app with Frida.
3. Fill in the login form: enter text in all fields, tap **Next**, enter a value in the OTP 2 field, and tap **Submit**.
4. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

The script hooks into text input controls at runtime and monitors when they lose focus. For each interaction, it captures the entered text, the input field class, accessibility identifier when available, placeholder text when available, and the `isSecureTextEntry` attribute. Based on this value, it reports whether the input is masked or exposed.

## Observation

{{ output.txt }}

The output contains all text that was entered in every text input, along with its masking status.

## Evaluation

The test case fails because the output shows the password field with `isSecureTextEntry` set to `false`, meaning it is exposed — and this field contains sensitive data.

- The password input (`password_field`) has `isSecureTextEntry=false` and contains sensitive data.
- The username input (`username_field`) has `isSecureTextEntry=false` but is not considered sensitive.
- The OTP 1 input (`otp_1_field`) has `isSecureTextEntry=true`, masking the sensitive data.
- The OTP 2 input (`OTP 2`) is a SwiftUI `SecureField` which always masks input. Notice that its `aid` is `null` because SwiftUI's `SecureField` does not propagate the `accessibilityIdentifier` to the underlying `UITextField`. However, `placeholder` correctly shows `OTP 2` and `isSecureTextEntry=true` confirms masking of the data.

!!! note
    Exposed fields display typed characters in plain text, while masked fields show bullet characters, so the test can also be verified visually by observing the on-screen behavior.
