---
title: Dynamic Analysis on Non-Rooted Devices
platform: android
---

If you don't have access to a rooted device, you can patch and repackage the target app to load a dynamic library at startup (e.g., the Frida gadget), enabling dynamic testing with Frida and related tools such as objection. Unlike iOS, Android apps aren't FairPlay-encrypted, so you can extract the APK directly from a device or download it from an alternative store without root access.

!!! note "Using the Android Emulator"
    For researchers and learners, the Android Emulator is often the simplest starting point. Apps running in the Emulator are not subject to the same hardware-backed restrictions as physical devices. You can attach Frida directly to the process without using the Frida Gadget (simply using `frida -U`), without patching the binary, and without re-signing the app. This makes the Emulator ideal for experimentation, scripting, and learning dynamic analysis techniques before moving to real-device testing. Keep in mind that the Emulator doesn't perfectly replicate real-device behavior, especially for hardware-backed features such as TEE/StrongBox, biometrics, certain hardware identifiers, and anti-emulator or root-detection checks.

The following sections walk through each step of the process for real non-rooted devices.

## Step 1: Obtain the APK

Follow @MASTG-TECH-0003 to obtain the APK for the app you want to test.

## Step 2: Inject the Frida Gadget

Follow @MASTG-TECH-0041 to patch the APK and inject the Frida Gadget library. Tools such as @MASTG-TOOL-0038 can automate most of this process as described in @MASTG-TECH-0004.

## Step 3: Sign the APK

Follow @MASTG-TECH-0039 to re-sign the patched APK. A standard Android debug keystore is sufficient; no developer account or special certificate is required.

## Step 4: Install the App

Follow @MASTG-TECH-0005 to install the re-signed APK on your device. If the original app is already installed, you'll need to uninstall it first, as Android rejects installations where the signing certificate has changed.
