---
title: Preventing Keyboard Caching for Sensitive Text Inputs
alias: preventing-keyboard-caching-for-sensitive-text-inputs
id: MASTG-BEST-0026
platform: ios
---

Sensitive text inputs should never participate in keyboard learning or prediction. On iOS, this means explicitly disabling features that allow the system keyboard to cache or reuse previously entered values. By default, standard text fields are eligible for caching unless told otherwise, which makes explicit configuration essential for any field that handles secrets or identifiers.

- For inputs such as usernames, email addresses, recovery codes, and similar data, set `autocorrectionType` to `no` and `spellCheckingType` to `no`. Autocorrection and spell checking rely on analyzing typed content and may retain or resurface that content through suggestions. Disabling both traits ensures the keyboard does not treat these values as language data worth remembering or reusing.

- For password-like inputs, always enable `isSecureTextEntry`. Secure text entry masks characters on screen and implicitly disables autocorrection and spell checking. This provides the strongest baseline protection for secrets and avoids accidental exposure through keyboard suggestions, predictive text, or third-party keyboards.

These traits must be applied consistently across the entire application. Configure them in code or Interface Builder for every security-sensitive field, including login, registration, account recovery, payment, and profile-related screens.

Any exceptions should be rare and well justified. If spell checking or autocorrection cannot be disabled due to strong usability requirements, the risk should be explicitly assessed and documented.
