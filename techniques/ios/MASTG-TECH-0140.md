---
title: Dynamic Analysis on Non-Jailbroken Devices
platform: ios
---

If you don't have access to a jailbroken device, you can patch and repackage the target app to load a dynamic library at startup (e.g., the Frida gadget), enabling dynamic testing with Frida and related tools such as objection. This way, you can instrument the app and perform everything needed for dynamic analysis, though you can't break out of the sandbox. However, this technique only works if the app binary isn't FairPlay-encrypted (i.e., obtained from the App Store).

The following sections walk through each step of the process.

## Step 1: Obtain the IPA

Follow @MASTG-TECH-0054 to obtain the IPA file for the app you want to test. If the binary is FairPlay-encrypted, you'll need a jailbroken device to decrypt it first before proceeding.

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
