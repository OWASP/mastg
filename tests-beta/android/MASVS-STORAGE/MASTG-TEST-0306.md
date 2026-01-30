---
title: Sensitive Data Stored Unencrypted via Android Room DB 
platform: android
id: MASTG-TEST-0306
type: [static, dynamic]
weakness: MASWE-0006
best-practices: []
profiles: [L1, L2]
status: placeholder
note: This test checks if the app uses the Android Room Persistence Library to store sensitive data (e.g., tokens, PII) without integrating an encryption layer (e.g., SQLCipher). It confirms the database file is stored in plaintext within the app's private sandbox.
---
