---
title: Preventing Screenshots and Screen Recording
alias: preventing-screenshots-and-screen-recording
id: MASTG-BEST-0014
platform: android
parent: None
---

Apps should prevent sensitive data from being captured from the UI, leaving the control of the user, and potentially leaking. This includes the following considerations:

- **System-generated snapshots** are sandboxed, but may still be accessible on rooted or compromised devices or through forensic acquisition.
- **User-initiated screenshots** are accessible to the user and to any app with storage or media access. They may also be automatically synced to cloud services such as Google Photos.
- **Screen recordings** can capture all visual content, including transient data such as passcodes or one-time codes, and may be stored or shared without the user's awareness.
- **Non-secure displays and remote screen sharing** can expose sensitive information to external monitors, casting devices, or remote control software.

Depending on the threat model of the app, one or more protections may be required. For example, on-screen keyboards or custom keypad views should be secured to prevent keystroke leakage from passcode fields. A banking app should ensure that account balances or transaction details are never visible in captured images, unless the user explicitly chooses to allow screenshots.

**Consider adding an in-app setting** to enable or disable this behavior, giving users control without compromising security by default.
