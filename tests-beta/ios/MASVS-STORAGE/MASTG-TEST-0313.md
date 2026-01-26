---
platform: ios
title: References to APIs for Preventing Keyboard Caching of Text Fields
id: MASTG-TEST-0313
type: [static]
weakness: MASWE-0053
profiles: [L2]
best-practices: [MASTG-BEST-0026]
knowledge: [MASTG-KNOW-0100]
---

## Overview

This test verifies whether the target app prevents sensitive information entered into text fields from being cached by the system keyboard. On iOS, the keyboard may suggest previously entered text when typing in any app on the device.

The test checks whether UI elements such as `UITextField`, `UITextView`, and `UISearchBar` prevent keyboard caching by using one or more of the following attributes:

- Setting [`UITextAutocorrectionType`](https://developer.apple.com/documentation/uikit/uitextautocorrectiontype)
- Enabling [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry)
- Setting [`spellCheckingType`](https://developer.apple.com/documentation/uikit/uitextinputtraits/spellcheckingtype)

**Note:** By default, text input is eligible for keyboard caching, and an app does not need to explicitly set `UITextAutocorrectionType` when creating a text field. Additionally, the UI may be configured in a Storyboard. As a result, this test may miss many true positives. For complete coverage, using @MASTG-TEST-0314 is recommended.

**Note:** This test may produce false negatives if the app uses custom text input controls that do not rely on standard UIKit classes such as `UITextField` or `UITextView`, for example in custom UI frameworks or game engines, or if text entry is handled through nonstandard abstractions that prevent reliable observation of input traits at rest.

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0072 to look for references to APIs that set the relevant input attributes.
3. Use @MASTG-TECH-0076 to analyze the relevant code paths and determine the values assigned to these attributes.

## Observation

The output should contain a list of locations where the app:

- creates text input fields such as `UITextField`, `UITextView`, or `UISearchBar`, and
- explicitly configures input attributes that prevent keyboard caching for security sensitive text fields.

## Evaluation

The test fails if any UI inputs that may handle sensitive values, for example usernames, passwords, email addresses, credit card numbers, or recovery codes, are eligible for keyboard caching. This occurs when:

- `isSecureTextEntry` is not enabled, or
- `autocorrectionType` is set to `default` or `yes`, or
- `spellCheckingType` is set to `default` or `yes`.

The test passes if all security-sensitive inputs are protected against keyboard caching.

**Note:** Depending on the app's threat model, some text fields may not require disabling spell checking. However, since enabling `isSecureTextEntry` implicitly disables both autocorrection and spell checking, and since explicit guarantees are otherwise limited, it is generally advisable to disable all three attributes for any text field that may handle sensitive information.
