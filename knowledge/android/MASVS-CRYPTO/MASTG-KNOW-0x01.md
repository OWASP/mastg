---
masvs_category: MASVS-CRYPTO
platform: android
title: Message Digests in the Java Cryptography Architecture
---

Android exposes hash functions through the Java Cryptography Architecture (JCA) [`MessageDigest`](https://developer.android.com/reference/java/security/MessageDigest) class. Applications select an algorithm with `MessageDigest.getInstance`. Overloads either use the first registered provider that implements the requested algorithm or select a provider by name or object.

Android's `MessageDigest` implementation supports MD5, SHA-1, and the SHA-2 family, including SHA-224, SHA-256, SHA-384, and SHA-512. Algorithm names and aliases are defined by the Java Security Standard Algorithm Names specification. The selected instance exposes its canonical algorithm name and provider through `getAlgorithm` and `getProvider`.

A message digest processes input with `update` and completes the computation with `digest`. The result is a fixed-length byte array whose size depends on the selected algorithm.
