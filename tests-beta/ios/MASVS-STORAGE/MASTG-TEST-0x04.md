---
title: Hardcoded API Keys and Secrets in the App Package
platform: ios
id: MASTG-TEST-0x04
type: [static]
weakness: MASWE-0004
best-practices: [MASTG-BEST-0x02]
profiles: [L1, L2]
---

## Overview

Anything shipped inside the IPA is readable by anyone who obtains it. Extracting the app package and dumping the strings of the main binary requires no jailbreak and no special access, so an API key, token, or credential embedded in the app must be treated as public.

Secrets reach the package through several routes, and checking only one of them is a common gap:

- **Code constants**: string literals in Swift or Objective-C, which the compiler places in the `__TEXT.__cstring` section of the binary in plaintext.
- **`Info.plist` entries**: keys added for third-party SDKs, which ship as a readable property list in the bundle.
- **Bundled resources**: `.plist`, `.json`, `.strings`, and configuration files added to the app bundle, along with certificate and key files such as `.pem` or `.p12`.
- **Embedded frameworks**: credentials compiled into bundled `.framework` binaries or their resources.
- **Third-party SDKs**: keys required by bundled libraries, often placed in `Info.plist` or a resource file by the SDK's own setup instructions.

Note that this test looks for secrets present in the package. Cryptographic keys used in code are covered separately by @MASTG-TEST-0213 and key material in bundled files by @MASTG-TEST-0214.

## Steps

1. Use @MASTG-TECH-0058 to extract and explore the app package, including its bundled resources and frameworks.
2. Use @MASTG-TECH-0153 and @MASTG-TECH-0154 to retrieve and inspect the `Info.plist` files of the app and of any embedded frameworks.
3. Use @MASTG-TECH-0070 to extract the strings from the main binary and from bundled framework binaries.
4. Use @MASTG-TECH-0078 to search the extracted strings and resource files for credential patterns.

## Observation

The output should contain a list of locations where credential-like values appear, including the file or binary section and address for each.

## Evaluation

The test case fails if any value that authenticates the app or its user to a first- or third-party service is present in the package. This includes API keys, access tokens, client secrets, passwords, and private keys.

Confirm each finding before reporting it, since pattern-based searching cannot distinguish a live credential from a placeholder:

- Check whether the value is actually a credential rather than a public identifier. Some values that look sensitive are designed to be public, such as a Firebase `GOOGLE_APP_ID` or an OAuth client ID.
- Check whether the value is a placeholder, an example from documentation, or a revoked key left behind.
- Check whether the key is scoped and restricted. A key restricted to a specific bundle identifier, with a narrow API scope, has a smaller impact than an unrestricted one, but it is still exposed.

The test case passes if no credentials are present, for example because the app obtains them at runtime from a backend after verifying app and device integrity, or because the app calls the third-party service through a backend that holds the key. See @MASTG-BEST-0x02.
