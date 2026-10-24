---
title: Android RASP
platform: android
source: https://github.com/securevale/android-rasp
hosts: [android]
---

Android RASP (Runtime Application Self-Protection) is a library written in Kotlin and Rust that provides runtime security checks for Android applications. It detects emulators, debuggers, and root access using native code implementation to make hooking and bypassing more difficult. The library performs comprehensive checks including superuser indicators, test tags, root management and cloaking apps, writable system paths, suspicious device properties, emulator characteristics, debug flags, and debugger attachment detection.

Android RASP provides a sample app to demonstrate its capabilities, see @MASTG-APP-0033.

!!! info "Limitations"
    Like all client-side security measures, Android RASP can be bypassed by skilled attackers. Implement multiple checks throughout your app and consider server-side validation for critical security decisions. The native Rust implementation provides additional protection against common hooking frameworks but is not foolproof.

!!! warning "OWASP MAS Disclaimer"
    The OWASP Mobile Application Security Testing Guide (MASTG) does not endorse specific tools or libraries, which are included as examples. It is the responsibility of the tester to evaluate its suitability for their specific use case and to stay informed about any limitations or vulnerabilities associated with it. Always refer to the official repository for the latest information and updates.
