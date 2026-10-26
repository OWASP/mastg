---
title: Hardcoded API Keys and Secrets in the App Package
platform: android
id: MASTG-TEST-0x03
type: [static]
weakness: MASWE-0004
best-practices: [MASTG-BEST-0x02]
profiles: [L1, L2]
---

## Overview

Anything shipped inside the APK is readable by anyone who downloads it. Decoding resources and decompiling the DEX requires no rooted device and no special access, so an API key, token, or credential embedded in the app package must be treated as public.

Secrets reach the package through several routes, and checking only one of them is a common gap:

- **Code constants**: string literals in Kotlin/Java source, which end up in the DEX as plaintext strings.
- **Resources and assets**: `res/values/strings.xml`, raw assets, and configuration files bundled with the app. These survive code obfuscation, since obfuscators rename identifiers but do not remove resource values.
- **Build configuration**: values injected via `BuildConfig` fields or Gradle properties, which are compiled into the DEX as ordinary constants.
- **Native libraries**: literals compiled into bundled `.so` files.
- **Third-party SDKs**: keys required by bundled libraries, often placed in resources by the SDK's own setup instructions.

Note that this test looks for secrets present in the package. Cryptographic keys used in code are covered separately by @MASTG-TEST-0212.

## Steps

1. Use @MASTG-TECH-0007 to unpack the app package and decode its resources.
2. Use @MASTG-TECH-0013 to reverse engineer the app.
3. Use @MASTG-TECH-0019 to retrieve strings from the DEX and from any bundled native libraries.
4. Use @MASTG-TECH-0025 to search the decompiled code and the decoded resources for credential patterns, for example with @MASTG-TOOL-0110.

## Observation

The output should contain a list of locations where credential-like values appear, including the file and line for each.

## Evaluation

The test case fails if any value that authenticates the app or its user to a first- or third-party service is present in the package. This includes API keys, access tokens, client secrets, passwords, and private keys.

Confirm each finding before reporting it, since pattern-based searching cannot distinguish a live credential from a placeholder:

- Check whether the value is actually a credential rather than a public identifier. Some values that look sensitive are designed to be public, such as a Firebase `google_app_id` or an OAuth client ID.
- Check whether the value is a placeholder, an example from documentation, or a revoked key left behind.
- Check whether the key is scoped and restricted. A key restricted to a specific package name and signing certificate, with a narrow API scope, has a smaller impact than an unrestricted one, but it is still exposed.

The test case passes if no credentials are present, for example because the app obtains them at runtime from a backend after verifying app and device integrity, or because the app calls the third-party service through a backend that holds the key. See @MASTG-BEST-0x02.
