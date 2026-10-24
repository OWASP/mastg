---
title: Hardening Against Virtual Devices
alias: hardening-against-virtual-devices
id: MASTG-BEST-0053
platform: ios
knowledge: [MASTG-KNOW-0135, MASTG-KNOW-0088, MASTG-KNOW-0136, MASTG-KNOW-0087, MASTG-KNOW-0089]
---

Virtual devices, such as @MASTG-TOOL-0108 and newer research environments, allow target applications to be executed in controlled environments that may use custom system images, modified platform components, missing or simulated hardware capabilities, or instrumentation that is difficult for the app to detect. This enables advanced reverse-engineering techniques.

Do not confuse virtual devices with the iOS Simulator or with iPhone and iPad apps running on macOS. The iOS Simulator runs simulator builds, while virtual devices attempt to reproduce an iOS device environment for iOS device binaries. iPhone and iPad apps running on macOS use a separate official Mac App Store distribution path on Macs with Apple silicon. See @MASTG-KNOW-0088 and @MASTG-KNOW-0136.

Defending against virtual devices requires a layered approach. Local checks should be treated as risk signals, not as standalone security decisions, because attackers can hook, patch, or bypass client-side logic. For sensitive functionality, combine client-side indicators with server-side validation and policy decisions.

Common controls include:

- **Virtual device detection**: Check for virtual device indicators such as missing hardware capabilities, inconsistent device properties, known virtualization artifacts, and App Attest validation failures. See @MASTG-KNOW-0135.
- **Reverse engineering tool detection**: Detect debuggers, instrumentation frameworks, hooking tools, and other runtime manipulation techniques, because virtual devices are often used together with these tools. See @MASTG-KNOW-0087.
- **Server-side enforcement**: Use server-side checks for high-risk actions, such as App Attest validation, app integrity validation, request risk scoring, throttling, step-up verification, or denying access to sensitive features when multiple high-risk signals are present.
- **Obfuscation and diversification**: Obfuscate detection logic, scatter checks throughout the app, vary their timing, and avoid relying on a single static indicator. See @MASTG-KNOW-0089.
- **Graceful response handling**: Avoid exposing precise detection reasons to the client. Prefer generic errors, reduced functionality, delayed responses, or server-side policy decisions that do not reveal which specific check was triggered.

These controls do not make execution in virtual devices impossible. Their goal is to increase the cost of analysis, reduce the reliability of automated large-scale testing, and make bypasses harder to maintain across app versions and environments.
