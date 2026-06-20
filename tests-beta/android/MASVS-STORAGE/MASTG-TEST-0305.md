---
title: Sensitive Data Stored Unencrypted via DataStore 
platform: android
id: MASTG-TEST-0305
type: [static, dynamic]
weakness: MASWE-0006
best-practices: []
profiles: [L1, L2]
status: placeholder
note: This test checks if the app uses the modern Jetpack DataStore API (Preferences DataStore or Proto DataStore) to store sensitive data (e.g., tokens, PII) without encryption. It confirms the absence of secure serializers or mechanisms to protect data integrity and confidentiality.
---
