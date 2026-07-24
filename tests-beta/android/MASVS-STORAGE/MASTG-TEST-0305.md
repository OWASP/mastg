---
platform: android
title: Static Analysis for Unencrypted Sensitive Data in DataStore
id: MASTG-TEST-0305
type: [static]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0025]
profiles: [L2]
status: new
---

## Overview

This test verifies whether the app's code uses Jetpack DataStore APIs to store sensitive data — such as tokens, passwords, or PII — without encryption. By default, both Preferences DataStore (`.preferences_pb`) and Proto DataStore (`.proto`) persist data in plaintext.

## Steps

1. Obtain the application package (APK) using @MASTG-TECH-0003.

2. Use static analysis (@MASTG-TECH-0014) to review code for calls to DataStore APIs, including:
   - `androidx.datastore.preferences.preferencesDataStore`
   - `androidx.datastore.core.DataStore` or usage of generated Proto classes
   - `dataStore.edit`, `updateData`, or `write` operations

3. Inspect whether:
   - sensitive data is stored using default, unencrypted DataStore,
   - no encryption layer (e.g., `EncryptedFile.Builder` for Preferences DataStore or encrypted custom serializer for Proto DataStore) is applied to sensitive fields.

## Observation

- Identify DataStore files referenced in the code.
- Determine whether sensitive data is stored without encryption.

## Evaluation

The test fails if the app references DataStore APIs and stores sensitive data without applying encryption.
