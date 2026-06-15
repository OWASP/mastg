---
platform: android
title: Runtime Use of Hook Detection Techniques
id: MASTG-TEST-0341
type: [dynamic, hooks]
weakness: MASWE-0107
best-practices: [MASTG-BEST-0041]
profiles: [R]
knowledge: [MASTG-KNOW-0030, MASTG-KNOW-0032, MASTG-KNOW-0118]
---

## Overview

This test verifies whether the app detects and responds to instrumentation and hooking attempts at runtime. For example, if the app does not terminate immediately when the following methods are called:

- Authentication tokens, OAuth tokens, session credentials, and stored account passwords could be extracted if [`AccountManager.getPassword()`](https://developer.android.com/reference/kotlin/android/accounts/AccountManager#getpassword), [`AccountManager.getAuthToken()`](https://developer.android.com/reference/kotlin/android/accounts/AccountManager#getauthtoken) are hooked.
- Cryptographic keys and certificates could be extracted if [`KeyStore.getKey()`](https://developer.android.com/reference/kotlin/java/security/KeyStore#getkey), [`KeyStore.getCertificate()`](https://developer.android.com/reference/kotlin/java/security/KeyStore#getcertificate) are hooked.
- Ephemeral/Session Keys could be extracted if [`Cipher.doFinal()`](https://developer.android.com/reference/kotlin/javax/crypto/Cipher#dofinal) is hooked.
- Database contents could be extracted if [`SQLiteDatabase.rawQuery()`](https://developer.android.com/reference/kotlin/android/database/sqlite/SQLiteDatabase#rawquery), [`SQLiteDatabase.query()`](https://developer.android.com/reference/kotlin/android/database/sqlite/SQLiteDatabase#query), [`SQLiteDatabase.execSQL()`](https://developer.android.com/reference/kotlin/android/database/sqlite/SQLiteDatabase#execsql) are hooked.
- Encrypted data could be extracted if [`EncryptedSharedPreferences`](https://developer.android.com/reference/kotlin/androidx/security/crypto/EncryptedSharedPreferences) APIs are hooked.
- Authentication could be bypassed if [`KeyGenParameterSpec.Builder.setUserAuthenticationRequired()`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setuserauthenticationrequired) is hooked.
- Any other function that processes or returns sensitive data is hooked.

!!! warning
    This list is just indicative, and each app may have its own defensive response mechanisms.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain one of the following:

- The expected hook callback data (e.g., function arguments, return values).
- Session termination, script errors, empty responses, or absence of expected hook data.

## Evaluation

The test case fails if the hook executes successfully and returns the expected data, indicating the app lacks runtime integrity verification.

The test case passes if the hooking attempt fails due to the app's defensive response (e.g., session terminates unexpectedly, hook callbacks never execute, or the process exits).

!!! note
    Even if the test case passes, it might still be possible to bypass the app's defensive response. @MASTG-KNOW-0030 and @MASTG-KNOW-0032 describe such challenges.
