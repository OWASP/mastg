---
title: Sensitive Data Stored Unencrypted via SQLite 
platform: android
id: MASTG-TEST-0304
type: [static, dynamic]
weakness: MASWE-0006
best-practices: []
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0037]
status: placeholder
note: This test checks if the app uses the default SQLite API (e.g., `SQLiteOpenHelper`, `context.openOrCreateDatabase`) to store sensitive data (e.g., tokens, PII) in an unencrypted database file within the app's sandbox. It confirms the absence of secure alternatives like SQLCipher or encrypted databases.
---
