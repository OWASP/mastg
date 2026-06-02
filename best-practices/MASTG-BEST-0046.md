---
title: Hardening Against Emulation
alias: hardening-against-emulation
id: MASTG-BEST-0046
platform: android
knowledge: [MASTG-KNOW-0031, MASTG-KNOW-0035, MASTG-KNOW-0033, MASTG-KNOW-0030]
---

Emulated devices allow target applications to be executed in controlled environments that may use custom system images, modified platform components, or instrumentation that is difficult for the app to detect. This enables advanced reverse-engineering techniques.

Defending against emulated devices involves a layered approach that commonly consists of applying several types of security controls:

- **Detective controls**: Scan for common device emulator indicators and properties (@MASTG-KNOW-0031) and use the Google Play Integrity API (@MASTG-KNOW-0035) to help identify risky devices, emulated environments, modified app binaries, and other untrusted interactions.
- **Deterrent controls**: Obfuscate this detection logic (@MASTG-KNOW-0033), scatter checks throughout the app, and vary their timing to increase the cost and effort required to bypass these checks.
- **Hardening against reverse-engineering tools**: Implement detection of reverse-engineering tools (@MASTG-KNOW-0030), as custom or emulated environments are often combined with such tools.
