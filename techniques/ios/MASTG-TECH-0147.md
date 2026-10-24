---
title: Patching
platform: ios
---

Making small changes to an iOS app can help overcome common obstacles during security testing and reverse engineering. On iOS, two issues in particular happen regularly:

1. You can't intercept HTTPS traffic with a proxy because the app implements SSL pinning.
2. You can't attach a debugger to the app because it lacks the `get-task-allow` entitlement.

In most cases, both issues can be fixed by patching the app and then re-signing and repackaging it. High-risk applications, such as financial apps or games that try to prevent cheating, often implement additional integrity checks beyond default iOS code-signing. In those cases, you have to patch the additional checks as well.

The first step is to obtain and extract the IPA file as described in @MASTG-TECH-0054.

!!! note
    If the app binary is encrypted (apps from the App Store), you must first decrypt it before patching. See the decryption section in @MASTG-TECH-0054 for details.

## Patching Example: Making an App Debuggable

By default, apps available on the Apple App Store are not debuggable. To debug an iOS application, it must have the `get-task-allow` entitlement enabled. This entitlement allows other processes (like a debugger) to attach to the app. Xcode doesn't add the `get-task-allow` entitlement in a distribution provisioning profile; it is only included in development provisioning profiles.

When reverse engineering apps, you'll often only have access to the release build. Release builds aren't meant to be debugged. Although this is a security feature, being able to attach a debugger and inspect the runtime state of a program makes understanding the program significantly easier.

To enable debugging on a release build, you need to re-sign the app with a development provisioning profile that includes the `get-task-allow` entitlement.

### Automated

To re-sign the decrypted IPA with debugging privileges use @MASTG-TOOL-0102. Select the decrypted.ipa file, choose the "Apple Development" certificate, and select the "iOS Team Provisioning Profile" matching your bundle ID from Xcode.

Ensure **No get-task-allow** is unticked. If you leave it checked, debugging will be disabled. Once you press **Start**, the tool will re-sign your IPA.

### Manual Steps

1. **Obtain a development provisioning profile**: Follow the steps in @MASTG-TECH-0079 to obtain a valid development provisioning profile. The profile will automatically include the `get-task-allow` entitlement set to `true`.

2. **Extract the IPA**: Unzip the IPA file to access its contents:

    ```bash
    unzip target_app.ipa -d extracted_app
    ```

3. **Verify the entitlements**: You can inspect the current entitlements of the app binary using @MASTG-TECH-0111 with @MASTG-TOOL-0114:

    ```bash
    codesign -d --entitlements - "extracted_app/Payload/TargetApp.app/TargetApp"
    ```

    For release builds from the App Store, you will typically see that `get-task-allow` is either missing or set to `false`.

4. **Re-sign the app**: Use your development provisioning profile to re-sign the app. The provisioning profile contains the `get-task-allow` entitlement. Follow the signing instructions in @MASTG-TECH-0092 to complete this step.

    The re-signing process will apply the entitlements from your development provisioning profile to the app, including `get-task-allow` set to `true`.

5. **Repackage the IPA**: After re-signing, repackage the modified app:

    ```bash
    cd extracted_app
    zip -r ../patched_app.ipa Payload
    ```

### Running the app

**Install and launch in debug mode**: Install the patched app on your device as described in @MASTG-TECH-0056, then launch it in debug mode following @MASTG-TECH-0055 and attaching @MASTG-TOOL-0057.

### Verification

To verify that the `get-task-allow` entitlement is now present, check the entitlements of the re-signed app using @MASTG-TECH-0111:

```bash
codesign -d --entitlements - "extracted_app/Payload/TargetApp.app/TargetApp"
```

You should see:

```xml
<key>get-task-allow</key>
<true/>
```

## Patching Binary Code

In some cases, you may need to patch the app's binary code directly, for example, to bypass certificate pinning checks or disable jailbreak detection. Tools like @MASTG-TOOL-0031 and @MASTG-TOOL-0033 can help you analyze and understand the binary, while tools like @MASTG-TOOL-0059 can modify the binary by adding or changing load commands.

For more advanced binary patching, consider using disassemblers for static analysis or writing custom Frida scripts to hook and modify behavior at runtime instead of patching the binary directly.

## Patching React Native Apps

If the app uses React Native, see @MASTG-TECH-0098 for specific guidance on patching React Native applications.
