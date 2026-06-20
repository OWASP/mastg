---
title: Continuous Anti-Debugging Checks
alias: continuous-anti-debugging-checks
id: MASTG-BEST-0047
platform: android
knowledge: [MASTG-KNOW-0028]
---

Implement frequent anti-debugging checks during sensitive execution paths instead of relying on a one-time check at startup.

A one-time check is easy to bypass because an attacker can attach a debugger after initialization and continue analysis without triggering the initial detection logic. Re-checking debugger state at runtime raises attacker effort and improves resilience.

On Android, you can combine Java and native checks depending on your threat model. For example, use [`Debug.isDebuggerConnected()`](https://developer.android.com/reference/android/os/Debug#isDebuggerConnected()) in Java flows and complement it with native checks in sensitive JNI paths.

Furthermore, tight continuous polling loops can be resource heavy. A good balance is to implement checkpoint-based checks before sensitive actions (for example, key use, payment approval, privileged API calls, or secrets access) and short periodic checks only while sensitive flows are active. This model reduces overhead while still making post-start debugger attachment harder.

Treat anti-debugging as a defense-in-depth control, not as a standalone guarantee. Advanced attackers can still bypass checks through patching, instrumentation, or hooks.
