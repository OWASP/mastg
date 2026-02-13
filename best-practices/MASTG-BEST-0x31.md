---
title: Store Data Encrypted in App Sandbox Directory
alias: store-data-encrypted-in-the-app-sandbox-directory
id: MASTG-BEST-0024
platform: ios
knowledge: [MASTG-KNOW-0036]
---

Apps should use `EncryptedSharedPreferences` or other secure storage mechanisms when storing sensitive data such as user credentials, authentication tokens, API keys, or personally identifiable information (PII). If the app writes sensitive data using the standard `SharedPreferences` API without encryption, the data can be easily accessed and exploited by attackers.
