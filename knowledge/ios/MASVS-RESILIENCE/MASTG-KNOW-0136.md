---
masvs_category: MASVS-RESILIENCE
platform: ios
title: iOS Apps Running on macOS Detection
---

## Overview

In the context of anti-reversing, detecting iPhone and iPad apps running on macOS is different from detecting the iOS Simulator or virtual devices.

On Macs with Apple silicon, [compatible iPhone and iPad apps can be distributed through the Mac App Store and run directly on macOS](https://developer.apple.com/documentation/apple-silicon/running-your-ios-apps-in-macos). Apple describes this as running the app without a porting process, using the same frameworks, resources, and runtime environments used on iOS and iPadOS.

This is an official distribution path and should not be treated as evidence that the app was repackaged, tampered with, or executed in the iOS Simulator.

From a resilience perspective, an app might still want to identify this environment because a Mac gives the user access to desktop tooling, a different input model, and a different set of hardware capabilities than an iPhone or iPad. Public research has shown attempts to use [Frida against iOS apps running on M1 Macs](https://github.com/frida/frida/issues/1734), and [another writeup shows that Frida could list such an app process but failed to attach](https://forensicmike1.com/post/taking-a-look-at-ios-apps-on-an-m1-mac/) until macOS-specific restrictions were addressed, including [System Integrity Protection](https://developer.apple.com/documentation/security/disabling-and-enabling-system-integrity-protection). [Frida's macOS documentation](https://frida.re/docs/examples/macos/) also states that Frida needs authorization to use [`task_for_pid`](https://developer.apple.com/documentation/bundleresources/entitlements/com.apple.security.cs.debugger) to access a target process, and that SIP may need to be disabled.

!!! note
    Do not confuse iPhone and iPad apps running on macOS with:

    - The iOS Simulator, which runs simulator builds, while iPhone and iPad apps on Apple silicon Macs run through an official Mac App Store distribution path. See @MASTG-KNOW-0088.
    - Virtual devices, which attempt to reproduce an iOS device environment for iOS device binaries. See @MASTG-KNOW-0135.

!!! warning "Security Considerations"

    Detection of iPhone and iPad apps running on macOS is a client-side control. It can be bypassed by changing the control flow, hooking `ProcessInfo`, or patching the binary.

    Use this signal as part of a defense-in-depth strategy. For sensitive functionality, combine local environment checks with server-side validation, integrity checks, and policy decisions that do not depend only on a value returned by the client.

### Runtime Environment Check

The most direct way to detect this environment is to use [`ProcessInfo.processInfo.isiOSAppOnMac`](https://developer.apple.com/documentation/foundation/processinfo/isiosapponmac), which indicates that the current process is an iPhone or iPad app running on a Mac:

```swift
private static func isiOSAppRunningOnMac() -> Bool {
    if #available(iOS 14.0, *) {
        return ProcessInfo.processInfo.isiOSAppOnMac
    }

    return false
}
```

This is only an indicator in adversarial settings because the result can be modified by hooking or patching the app.

### Distinguishing from Mac Catalyst

Mac Catalyst is a separate case. A Mac Catalyst app is a Mac app built from an iPad app codebase, while an iPhone or iPad app running on macOS is still the iPhone or iPad app running on a Mac.

If the goal is to detect whether the app is running in any Mac execution context, combine `isiOSAppOnMac` with `isMacCatalystApp`.

```swift
private static func isRunningOnMac() -> Bool {
    if #available(iOS 14.0, *) {
        return ProcessInfo.processInfo.isiOSAppOnMac ||
               ProcessInfo.processInfo.isMacCatalystApp
    }

    if #available(iOS 13.0, *) {
        return ProcessInfo.processInfo.isMacCatalystApp
    }

    return false
}
```

### Compiler Directives

Compiler directives for Mac Catalyst do not detect iPhone and iPad apps running directly on Apple silicon Macs.

```swift
#if targetEnvironment(macCatalyst)
    // This code will only be compiled for Mac Catalyst builds.
#endif
```

An iPhone or iPad app running on macOS through the Mac App Store should be detected at runtime with `ProcessInfo.processInfo.isiOSAppOnMac`.

### Distribution-Level Control

If an app should not be made available to users on Macs with Apple silicon, the preferred control is to [disable this availability in App Store Connect](https://developer.apple.com/help/app-store-connect/manage-your-apps-availability/manage-availability-of-iphone-and-ipad-apps-on-macs-with-apple-silicon/) rather than relying only on a runtime check.

App Store Connect allows developers to opt out of offering an iPhone or iPad app on the Mac App Store for Apple silicon Macs. This prevents official distribution through that channel, but it does not replace tamper detection or other resilience controls for repackaged or sideloaded app instances.
