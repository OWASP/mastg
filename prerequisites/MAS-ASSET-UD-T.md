---
id: MAS-ASSET-UD-T
title: User Data in Transit
---

## MAS-ASSET-UD-T: User Data in Transit

This asset category covers user data that is transmitted between the application and a remote endpoint, or between processes on the device. User data in transit includes any personal, financial, health, or authentication information exchanged over a network or via interprocess communication (IPC).

### Examples

- PII sent to backend services (e.g., names, Social Security numbers, account details submitted in forms)
- User credentials or PINs transmitted during login or account recovery flows
- Session tokens, JWTs, or OAuth tokens included in API request headers or bodies
- Financial or health data sent to remote servers for processing or storage
- Key material exchanged during authentication or key agreement protocols with a backend
