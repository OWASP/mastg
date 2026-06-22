---
title: Implementing Source Code Integrity Checks on iOS
alias: implementing-source-code-integrity-checks-ios
id: MASTG-BEST-0x03
platform: ios
knowledge: [MASTG-KNOW-0x01]
---

Implement source code integrity checks in iOS apps to detect unauthorized modifications to the app binary. These checks raise the cost for attackers who try to tamper with the app, especially on jailbroken devices or when the app is re-signed with another certificate.

The OS provides code signing to verify the authenticity and integrity of app binaries before launch. However, on jailbroken devices or when an attacker re-signs the app with their own certificate, this protection can be bypassed.

To supplement the OS-level protection, implement runtime source code integrity checks. These checks parse the [Mach-O binary structure](https://developer.apple.com/library/archive/documentation/DeveloperTools/Conceptual/MachOTopics/0-Introduction/introduction.html) to locate the `__TEXT/__text` section, compute a cryptographic hash over it, and compare the hash against a hardcoded or securely stored reference value (see @MASTG-KNOW-0x01 for an implementation example).

Use a strong hash function such as SHA-256 (via `CC_SHA256` from CommonCrypto) instead of MD5, which is cryptographically weak.

Store the reference hash value in a location that is itself protected from modification (for example, hardcoded in an obfuscated form in the binary).

!!! warning
    Runtime source code integrity checks are inherently bypassable on jailbroken devices. Attackers can hook the check itself, patch the reference hash, or use Frida to intercept file-system calls and return the original binary. These checks should be treated as a cost-raising measure, not a guarantee.
