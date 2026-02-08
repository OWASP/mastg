---
title: Android RASP Sample
platform: android
source: https://github.com/securevale/android-rasp
package: com.securevale.rasp.android
---

A sample application demonstrating the Android RASP (Runtime Application Self-Protection) library. The app showcases detection capabilities for root, emulators, and debuggers. The library implements checks using native code written in Rust to make bypassing more difficult. Provides a test interface to evaluate various RASP detection techniques including root indicators, emulator characteristics, and debugger attachment.

> The library offers granular control over individual checks and supports subscribing to vulnerability notifications. Native code implementation enhances resistance to hooking frameworks. See the [Android RASP repository](https://github.com/securevale/android-rasp) for detailed usage and configuration options.
