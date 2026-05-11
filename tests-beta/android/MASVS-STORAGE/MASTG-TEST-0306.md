---
platform: android
title: Static Analysis for Unencrypted Sensitive Data in Android Room DB
id: MASTG-TEST-0306
type: [static]
weakness: MASWE-0006
best-practices: [MASTG-BEST-0025]
profiles: [L2]
status: new
---

## Overview

This test verifies whether the app's code uses the [Android Room Persistence Library](https://developer.android.com/training/data-storage/room) to store sensitive data — such as tokens, credentials, or PII — without encryption. By default, Room stores data in unencrypted SQLite databases.

## Steps

1. Obtain the application package (APK) using @MASTG-TECH-0003.

2. Use static analysis (@MASTG-TECH-0014) to identify references to Room APIs:
   - `androidx.room.Room`
   - `@Database`, `@Dao`, `@Entity` annotations
   - `databaseBuilder`, `build` calls, `SupportSQLiteOpenHelper.Factory` implementations

3. Inspect whether:
   - sensitive fields (tokens, secrets, PII) are stored in plaintext within `@Entity` classes
   - a secure factory or wrapper (e.g., SQLCipher implementation of `SupportSQLiteOpenHelper.Factory`) is explicitly applied to the database builder

## Observation

- Which Room database files are referenced in the code
- Whether encryption is being applied in the code to the sensitive data before being stored

## Evaluation

The test fails if the app stores sensitive data in Room databases without encryption (e.g., SQLCipher or equivalent) applied in the builder.
