---
title: Use Secure Random Number Generator APIs
alias: ios-use-secure-random
id: MASTG-BEST-0025
platform: ios
knowledge: [MASTG-KNOW-0070]
---

Use secure random number generator APIs that are backed by the operating system _cryptographically secure pseudorandom number generator (CSPRNG)_. Do not build your own _pseudorandom number generator (PRNG)_.

## Swift / Objective-C

- **Security Framework (preferred)**: Use the [`SecRandomCopyBytes`](https://developer.apple.com/documentation/security/secrandomcopybytes(_:_:_:)) API from the Security framework, which produces cryptographically secure random bytes backed by the system CSPRNG.
- **CommonCrypto**: You _could_ use `CCRandomCopyBytes` or `CCRandomGenerateBytes` (not documented on the Apple Developers website), which are also backed by the system CSPRNG. However, prefer `SecRandomCopyBytes` which is a wrapper around these functions.
- **Swift Standard Library**: You can use the Swift Standard Library `.random` APIs which are backed by `SystemRandomNumberGenerator`. However, note that their random number generator can be customized, so ensure you use the default `SystemRandomNumberGenerator` (e.g., by not specifying a custom generator) or a secure alternative (ensure it is cryptographically secure).
- **CryptoKit**: CryptoKit doesn't expose a direct random byte generator, but it provides secure random nonces and keys through its cryptographic operations, which are backed by the system CSPRNG. For example, you can use `SymmetricKey` for keys and `AES.GCM.Nonce` for nonces without needing to manage raw random bytes directly.

See @MASTG-KNOW-0070 for code examples of these APIs.

## Other Languages

Consult the standard library or framework to locate the API that exposes the operating system CSPRNG. This is usually the safest path, provided the library itself has no known weaknesses.

For cross-platform or hybrid apps on iOS rely on frameworks that forward calls to the underlying system CSPRNG. For example:

- In Flutter or Dart use [`Random.secure()`](https://api.flutter.dev/flutter/dart-math/Random/Random.secure.html), which is documented as cryptographically secure. It reaches `SecRandomCopyBytes` through [the platform integration layers](https://github.com/dart-lang/sdk/blob/47e77939fce74ffda0b7252f33ba1ced2ea09c52/runtime/bin/crypto_macos.cc#L16). See [this article](https://www.zellic.io/blog/proton-dart-flutter-csprng-prng/) for a security review.
- In React Native use a library such as [`react-native-secure-random`](https://github.com/robhogan/react-native-securerandom) or [`react-native-get-random-values`](https://github.com/LinusU/react-native-get-random-values), which internally calls `SecRandomCopyBytes` on iOS.
