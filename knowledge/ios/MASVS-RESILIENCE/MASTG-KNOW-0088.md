---
masvs_category: MASVS-RESILIENCE
platform: ios
title: iOS Simulator Detection
---

## Overview

In the context of anti-reversing, the goal of emulator and virtual device detection is to increase the difficulty of running the app outside the expected device environment. This increased difficulty forces the reverse engineer to defeat the checks or use a physical device, thereby limiting the access required for large-scale device analysis.

Apple provides [Simulator through Xcode](https://developer.apple.com/documentation/safari-developer-tools/installing-xcode-and-simulators). Simulator does not emulate the complete hardware of an iOS device. Instead, it runs apps built for a simulated device destination. On Apple silicon, simulator builds can also use the `arm64` CPU architecture. However, CPU architecture alone does not make simulator and device builds interchangeable. An `arm64` simulator binary is built for the `iphonesimulator` SDK, while an `arm64` device binary is built for the `iphoneos` SDK.

Therefore, apps distributed through the App Store normally do not need to detect the iOS Simulator, because App Store device builds are not Simulator builds. Apple documents that [Xcode cannot create an archive when the run destination is a simulator](https://help.apple.com/xcode/mac/current/en.lproj/devf37a1db04.html), and that iOS app archives should use a build only destination such as Generic iOS Device.

However, this does not mean that iPhone and iPad App Store apps can only run on physical iOS devices. On Macs with Apple silicon, [compatible iPhone and iPad apps can be distributed through the Mac App Store and run directly on macOS](https://developer.apple.com/documentation/apple-silicon/running-your-ios-apps-in-macos). This environment is different from the iOS Simulator and should be detected separately. For more information, check @MASTG-KNOW-0136.

!!! note
    Do not confuse the iOS Simulator with virtual devices or with iPhone and iPad apps running on macOS. App Store apps cannot run in the iOS Simulator as normal device builds, but they can run in virtual devices when those environments can execute iOS device binaries. For more information on virtual devices, check @MASTG-KNOW-0135.

Although App Store apps cannot normally be executed in the iOS Simulator, simulator builds can identify that they are running in the Simulator using several indicators.

### Runtime Environment Check

The following indicator uses [`ProcessInfo.processInfo.environment`](https://developer.apple.com/documentation/foundation/processinfo/environment) to check whether the environment variable `SIMULATOR_DEVICE_NAME` has been set.

```swift
private static func isSimulatorEnv() -> Bool {
    return ProcessInfo.processInfo.environment["SIMULATOR_DEVICE_NAME"] != nil
}
```

This is only an indicator because environment variables can be modified or spoofed in controlled testing environments.

### Usage of Compiler Directives

Swift compiler directives can be used so that the compiler adds specific code to the binary when the destination target is an iOS Simulator. Apple documents [`targetEnvironment(simulator)`](https://developer.apple.com/documentation/xcode/running-code-on-a-specific-version#Compile-code-for-a-specific-platform) for compiling code only for Simulator builds.

```swift
#if targetEnvironment(simulator)
    // This code will only be compiled for simulator binaries.
#endif
```

This is a compile-time check, not a runtime check. It is useful for separating simulator-specific code during development or testing, but it does not apply to production App Store device builds.
