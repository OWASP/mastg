---
masvs_category: MASVS-CODE
platform: ios
title: App Signing
---

[Code signing](../../../Document/0x06a-Platform-Overview.md#code-signing) your app assures users that the app has a known source and hasn't been modified since it was last signed. Before your app can integrate app services, be installed on a non-jailbroken device, or be submitted to the App Store, it must be signed with a certificate issued by Apple. For more information on how to request certificates and code sign your apps, review the [App Distribution Guide](https://developer.apple.com/library/content/documentation/IDEs/Conceptual/AppDistributionGuide/Introduction/Introduction.html "App Distribution Guide").

## Signing Requirements for Apps and Libraries

iOS enforces [mandatory code signing](https://support.apple.com/guide/security/app-code-signing-process-sec7c917bf14/web) on executable code. The app executable carries an embedded code signature, which includes a [CodeDirectory](https://developer.apple.com/library/archive/documentation/Security/Conceptual/CodeSigningGuide/RequirementLang/RequirementLang.html) containing hashes over executable code pages and other signed metadata. For app bundles, code signing also seals bundle resources in `_CodeSignature/CodeResources`, and nested executable code such as frameworks, dynamic libraries, and app extensions is signed separately and recorded as nested code in the containing bundle signature. App entitlements (see @MASTG-KNOW-0077) are embedded in the code signature of the executable and are therefore also protected by signing.

Before loading any binary — whether it is the app executable or a dynamic library, at launch or via a `dlopen()` call at runtime — iOS verifies that the binary is signed and allowed to run in that process:

1. **Signature validity**: the binary must carry a valid code signature from an Apple-issued certificate.

2. **Team ID validation**: dynamic libraries loaded by a process must either be Apple-signed platform libraries or have the same Team ID as the main executable.

> "At runtime, code signature checks of all executable memory pages are checked as they're loaded to help ensure that an app hasn't been modified since it was installed or last updated." — [Apple Platform Security](https://support.apple.com/guide/security/intro-app-security-ios-ipados-visionos-secf49cad4db/web)

These checks prevent an app from loading arbitrary native code at runtime. A dynamic library copied into the app container after installation is not treated as embedded code that was signed and installed as part of the app bundle, even if it is signed with the developer's own Team ID. Attempting to load it can fail with an error such as:

```txt
dlopen failed: ... no suitable image found. Did find:
    ...: code signing blocked mmap() of '<path>'
```

For App Store apps, this means **every native library that can ever load at runtime must have been present in the app bundle at installation time** and signed as part of the app. There is no supported mechanism for an App Store app to introduce new native code onto a device at runtime.

Static libraries are different from dynamic libraries. A static library is linked into the final app executable at build time, so its code is covered by the signature of the final Mach-O binary. It is not loaded as a separate signed binary at runtime.

For details on how to inspect code signature metadata, including the CodeDirectory format version, see @MASTG-TECH-0112.
