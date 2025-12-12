---
masvs_category: MASVS-STORAGE
platform: ios
title: Keyboard Cache
---

Several features such as autocorrection, spell checking, and predictive suggestions help users enter text more quickly. On iOS, these features are backed by on-device keyboard dictionaries that are persisted in `.dat` files under `/private/var/mobile/Library/Keyboard/` and its subdirectories. Forensic analyses and security research show that user-typed terms, including previously unknown words, can appear in files such as `dynamic-text.dat` in this directory and may be recoverable during device analysis.

The system uses the [`UITextInputTraits`](https://developer.apple.com/documentation/uikit/uitextinputtraits) protocol to control how keyboard assistance and learning behave for a given text control. Standard UIKit controls like [`UITextField`](https://developer.apple.com/documentation/uikit/uitextfield), [`UITextView`](https://developer.apple.com/documentation/uikit/uitextview), and [`UISearchBar`](https://developer.apple.com/documentation/uikit/uisearchbar) conform to this protocol and expose these traits directly.

Key traits that influence what the keyboard learns include:

- [`autocorrectionType`](https://developer.apple.com/documentation/uikit/uitextinputtraits/autocorrectiontype) (default: `UITextAutocorrectionType.default`, enabled): This property controls whether the system attempts to correct what the user types. With autocorrection enabled, the system analyzes entered text, tracks unfamiliar words, and proposes replacements, sometimes substituting them automatically unless the user rejects the suggestion. The default value delegates the decision to the current keyboard and input method, which typically results in autocorrection being enabled.
- [`spellCheckingType`](https://developer.apple.com/documentation/uikit/uitextinputtraits/spellcheckingtype) (default: `UITextSpellCheckingType.default`, enabled): This property controls spell checking behavior. When enabled, the system underlines suspected misspellings and offers alternatives. Spell checking uses the same underlying language services as autocorrection and contributes to recently observed words that influence suggestions and keyboard learning.
- [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry) (default: `false`, disabled): This property hides the displayed characters and is **intended for password-like inputs**. Apple documents that enabling secure text entry disables copy operations and suppresses keyboard features that analyze or transform input, including autocorrection and spell checking. While this behavior prevents secure fields from participating in normal keyboard learning and suggestion mechanisms, Apple does not provide a formal guarantee about all forms of internal storage or handling, and therefore secure text entry should be treated as a strong mitigation rather than an absolute guarantee.
