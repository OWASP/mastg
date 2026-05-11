---
title: Extracting Loaded Libraries
platform: ios
---

This technique describes how to enumerate the dynamic libraries loaded into memory by a running iOS app. Unlike @MASTG-TECH-0082, which identifies bundled libraries statically from the IPA, this approach requires the app to be running on a device.

## Code Signing and Its Implications for Library Loading

iOS enforces [mandatory code signing](https://support.apple.com/guide/security/app-code-signing-process-sec7c917bf14/web) on every binary that gets mapped into a process. Before loading any dynamic library (whether at launch or via a `dlopen()` call at runtime) `dyld` and the kernel perform two distinct checks:

1. **Signature validity and Team ID**: the library must have a valid code signature and its Team ID must match the main executable's Team ID or be an Apple-signed system library.

2. **Trust authorization**: the binary's code directory hash must be present in the device's [trust cache](https://support.apple.com/guide/security/trust-caches-sec7d38fbf97/web), a system-level record of binaries that are authorized to run. The trust cache is populated only through Apple-controlled installation mechanisms (App Store, TestFlight, or a provisioning profile). Binaries that were never installed through these mechanisms are absent from the trust cache and will be rejected regardless of their signature.

> "At runtime, code signature checks of all executable memory pages are checked as they're loaded to help ensure that an app hasn't been modified since it was installed or last updated." — [Apple Platform Security](https://support.apple.com/guide/security/intro-app-security-ios-ipados-visionos-secf49cad4db/web)

The trust cache check is what prevents loading a library that is not in the app's bundle, even if it is validly signed with the developer's own Team ID. Such a library passes the Team ID check but its code directory hash is not in the trust cache because it was never installed through an Apple-controlled mechanism. Attempting to load it fails unconditionally:

