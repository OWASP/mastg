---
title: Dynamic Analysis on Non-Jailbroken Devices
platform: ios
---

If you do not have a jailbroken test device, you can still perform dynamic analysis on a non-jailbroken iOS device by patching and repackaging the target app to load a dynamic library at startup, such as the Frida Gadget. This enables runtime instrumentation with tools like Frida or objection while remaining inside the standard iOS sandbox. However, this approach requires a non-encrypted app binary. If the app was obtained from the App Store, you must first decrypt it, which **requires temporary access to a jailbroken device**. If you already have a non-encrypted IPA, for example from a development, enterprise, or self-built project, no jailbreak is required at any stage.

!!! note "Using the iOS Simulator"
    For researchers and learners, the iOS Simulator is often the simplest starting point for testing apps you build and sign yourself, as you cannot install an IPA retrieved from the App Store. Apps running in the Simulator are not FairPlay-encrypted and are not subject to the same code-signing and runtime restrictions as physical devices. You can attach Frida directly to the process without using the Frida Gadget, without patching the binary, and without re-signing the app. This makes the Simulator ideal for experimentation, scripting, and learning dynamic analysis techniques before moving to real-device testing. Keep in mind that the Simulator does not perfectly replicate real-device behavior, especially for hardware-backed features such as Secure Enclave, biometrics, keychain protection classes, push notifications, and certain anti-debugging or jailbreak-detection checks.

The following sections walk through each step of the process for App Store apps:

## Step 1: Obtain the IPA

Follow @MASTG-TECH-0054 to obtain the IPA file for the app you want to test and ensure you obtain a **non-encrypted version before proceeding** (you'll need a jailbroken device).

## Step 2: Obtain a Developer Provisioning Profile

Follow @MASTG-TECH-0079 to create a signing identity and obtain a valid provisioning profile. You'll need this to sign the repackaged IPA so iOS allows it to run on your device.

## Step 3: Inject the Frida Gadget

Follow @MASTG-TECH-0090 to patch the IPA and inject the Frida Gadget library. Tools such as @MASTG-TOOL-0118 and @MASTG-TOOL-0038 can automate most of this process.

## Step 4: Sign the IPA

Follow @MASTG-TECH-0092 to re-sign the patched IPA using the provisioning profile and signing identity from Step 2.

## Step 5: Install the App

Follow @MASTG-TECH-0056 to install the signed IPA on your device. Note that because you've modified the IPA, the Bundle Identifier may have changed depending on the signing tool you used.

## Step 6: Launch the App in Debug Mode

Follow @MASTG-TECH-0055 to launch the repackaged app in debug mode. Launching via SpringBoard will cause it to crash; you must use the debug launch method so the Frida Gadget can start and wait for your connection.
