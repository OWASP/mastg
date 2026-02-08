---
title: RootBeer
platform: android
source: https://github.com/scottyab/rootbeer
hosts: [android]
---

RootBeer is an Android library for detecting root access on devices. It implements multiple detection techniques using both Java and native code (JNI) to make bypassing more difficult. The library checks for common root indicators including root management apps (SuperSU, Magisk), root cloaking apps, dangerous packages, test-keys in the build, dangerous system properties, the presence of su and BusyBox binaries, and read-write access to system partitions.

RootBeer provides a sample app to demonstrate its capabilities, see @MASTG-APP-0032.

!!! info "Limitations"
    Root detection cannot be 100% reliable since root access provides elevated privileges that can be used to hide root indicators. RootBeer can be bypassed by determined attackers, especially when using multiple root cloaking tools simultaneously. For critical security decisions, consider combining RootBeer with server-side validation using Google Play Integrity API.

!!! warning "OWASP MAS Disclaimer"
    The OWASP Mobile Application Security Testing Guide (MASTG) does not endorse specific tools or libraries, which are included as examples. It is the responsibility of the tester to evaluate its suitability for their specific use case and to stay informed about any limitations or vulnerabilities associated with it. Always refer to the official repository for the latest information and updates.
