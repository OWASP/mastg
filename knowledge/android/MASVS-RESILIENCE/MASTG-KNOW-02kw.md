---
masvs_category: MASVS-RESILIENCE
platform: android
title: Application Attestation via Hardware-Backed Key Attestation
---

The attestation extension data embedded in the leaf certificate of a Key Attestation (@MASTG-KNOW-0044) certificate chain also includes information about the application that created the attested key. This follows the [ASN.1 schema](https://source.android.com/docs/security/features/keystore/attestation#schema) and allows a remote server to verify the identity and integrity of the calling application.

## Application Identity

The `attestationApplicationId` field in the `softwareEnforced` section of the attestation extension contains the identity of the application that created the key:

- **`packageInfos`**: A set of package information entries, each containing:
    - **`packageName`**: The application's package name (e.g., `com.example.app`).
    - **`version`**: The application's version code.
- **`signatureDigests`**: The SHA-256 digests of the application's signing certificates. This allows the server to verify that the app was signed with the expected key, ensuring it has not been repackaged or tampered with.

## Server-Side Verification

When verifying application attestation, the server should check:

- The `packageName` matches the expected application identifier.
- The `signatureDigests` match the known signing certificate digests of the legitimate application. This ensures the key was created by a genuine, unmodified version of the app.
- The `version` is within an acceptable range, allowing the server to reject outdated or known-vulnerable versions.

Combined with device attestation (@MASTG-KNOW-01kw), this enables the server to establish that a request originates from a legitimate application running on a trustworthy device.
