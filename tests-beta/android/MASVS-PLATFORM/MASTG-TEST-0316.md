---
platform: android
title: References to APIs Hiding Sensitive Data in Text Input Fields
id: MASTG-TEST-0316
type: [static, code, manual]
weakness: MASWE-0036
profiles: [L2]
knowledge: [MASTG-KNOW-0x01]
---

## Overview

If the app does not mask text input fields that contain authentication data, such as passwords, PINs, or verification codes, the data may be exposed in plain text through the user interface.

This test statically analyzes the app for text input fields and their masking configuration to determine whether authentication data can be displayed in plain text. See @MASTG-KNOW-0x01 for the Android APIs and configuration options used to mask text input.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where the app creates or configures text input fields, including references that determine whether the entered text is masked.

## Evaluation

The test case fails if a text input field used for a password, PIN, or verification code is configured so that the value is displayed in plain text by default or without an intentional user action.

For example:

- An Android View used for authentication data does not use an appropriate password input type or transformation.
- A Compose `TextField` used for authentication data does not use an appropriate masking transformation, such as `PasswordVisualTransformation`.
- A `SecureTextField` or `BasicSecureTextField` used for authentication data is configured with `TextObfuscationMode.Visible`.
- The masking configuration is changed programmatically to expose the value without an intentional user action.

**Further Validation Required:**

Since determining which fields handle authentication data and whether an unmasked state is intentional is context-dependent, inspect each reported code location using @MASTG-TECH-0023 to determine whether the field handles a password, PIN, or verification code and whether its masking configuration can expose the value in plain text.

**Expected False Negatives:**

This test may produce false negatives if the app uses custom text input controls that do not rely on standard Android or Jetpack Compose APIs, for example controls implemented by third-party UI frameworks, cross-platform frameworks, game engines, or custom rendering code.
