---
masvs_category: MASVS-PRIVACY
platform: ios
title: iOS Permission Mechanisms
---

Entitlements are key-value pairs that grant an iOS app permission to use specific system services or capabilities beyond the default app sandbox. They are the mechanism through which apps access privileged resources such as HealthKit, HomeKit, VPN configuration, iCloud containers, Apple Pay, or App Groups.

On iOS, entitlements appear in two distinct locations, each serving a different purpose during app validation and execution. Beyond entitlements, apps must also declare purpose strings in `Info.plist` for any permission that requires explicit user consent.

## Embedded Entitlements (Code Signature)

Every signed iOS app binary contains entitlements embedded directly in its [code signature](https://developer.apple.com/documentation/security/code-signing-services). These are the entitlements that the system enforces at runtime. You can extract them from the binary using tools like `codesign` or @MASTG-TOOL-0073 without needing access to the provisioning profile.

These embedded entitlements represent the definitive set of capabilities the app has been signed with and are available regardless of the distribution method.

## Provisioning Profile Entitlements

The [provisioning profile](https://developer.apple.com/documentation/technotes/tn3125-inside-code-signing-provisioning-profiles) (`embedded.mobileprovision`) is a cryptographically signed property list issued by Apple's provisioning system. It contains an `Entitlements` dictionary that declares the capabilities Apple has authorized for the app.

The `embedded.mobileprovision` file is only present in apps built for:

- **Development** distribution
- **Ad Hoc** distribution
- **Enterprise** distribution

Apps distributed through the **App Store do not contain** the `embedded.mobileprovision` file. For App Store builds, Apple's servers validate entitlements during the submission and review process instead.

The provisioning profile can be decoded from its binary CMS format to XML using @MASTG-TOOL-0063.

## Comparing Both Sources

Both locations contain entitlement declarations, but they serve different roles:

- **Embedded entitlements** (from the binary) are always present and reflect what the system enforces at runtime.
- **Provisioning profile entitlements** (from `embedded.mobileprovision`) represent what Apple's provisioning system has authorized. The broader provisioning profile also contains additional context such as team identifiers, certificate references, and device UDIDs, but these are separate fields from the entitlements dictionary itself.

The two sets of entitlements generally overlap; however, the provisioning profile is only embedded in development, ad hoc, or enterprise builds and is not present in App Store–distributed builds.

## Info.plist Usage Descriptions

In addition to entitlements, iOS requires apps to declare **purpose strings** (also known as [usage descriptions](https://developer.apple.com/documentation/uikit/requesting-access-to-protected-resources)) in `Info.plist` for any permission that triggers a user-facing prompt. Each key contains a human-readable string that explains the reason why the app require access to a specific resource.

Common usage description keys include:

- `NSCameraUsageDescription` — camera access
- `NSLocationWhenInUseUsageDescription` — location access while using the app
- `NSLocationAlwaysAndWhenInUseUsageDescription` — location access at all times
- `NSContactsUsageDescription` — access to contacts
- `NSMicrophoneUsageDescription` — microphone access
- `NSPhotoLibraryUsageDescription` — photo library access

The system displays these strings in the permission dialog presented to the user. Each key expects a non-empty string value that serves as a rationale for the requested permission. If the corresponding usage description key is missing from `Info.plist`, the system terminates the app when it attempts to request the associated permission.
