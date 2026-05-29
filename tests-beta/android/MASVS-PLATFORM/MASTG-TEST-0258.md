---
platform: android
title: References to Keyboard Caching Attributes in UI Elements
id: MASTG-TEST-0258
type: [static, code]
weakness: MASWE-0053
best-practices: [MASTG-BEST-0019]
profiles: [L2]
knowledge: [MASTG-KNOW-0055]
---

## Overview

This test verifies that the app appropriately configures text input fields to prevent the keyboard from caching sensitive information, such as passwords or personal data.

Android apps can configure the behavior of text input fields using:

- From layout files within the `res/layout` directory:
    - Using the `android:inputType` XML attributes.
- Programmatically in the code:
    - By calling the `setInputType` method on input fields and passing appropriate input type values.
    - In Jetpack Compose, by using the [`KeyboardOptions` constructors](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/KeyboardOptions#public-constructors_1) and setting the `keyboardType` and `autoCorrect` parameters.

See section "Non-Caching Input Types" in @MASTG-KNOW-0055 for more details on the input types that prevent keyboard caching of sensitive information.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.
3. Use @MASTG-TECH-0007 to extract the layout files from the app package.

## Observation

The output should include:

- All `android:inputType` XML attributes, if using XML for the UI.
- All calls to the `setInputType` method and the input type values passed to it.

## Evaluation

The test case fails if there are any fields handling sensitive data for which the app does not use non-caching input types.
