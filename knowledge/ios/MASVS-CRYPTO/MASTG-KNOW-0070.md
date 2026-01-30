---
masvs_category: MASVS-CRYPTO
platform: ios
title: Random Number Generator
refs:
- https://developer.apple.com/documentation/security/secrandomcopybytes(_:_:_:)
- https://support.apple.com/en-us/guide/security/seca0c73a75b/web
- https://developer.apple.com/documentation/security/randomization-services
- https://blog.xoria.org/randomness-on-apple-platforms/
- https://forums.swift.org/t/random-data-uint8-random-or-secrandomcopybytes/56165
- https://github.com/apple-oss-distributions/Security/blob/main/OSX/libsecurity_keychain/lib/SecRandom.c
- https://developer.apple.com/videos/play/wwdc2019/709/?time=1295
- https://www.niap-ccevs.org/technical-decisions/TD0510
---

[Random number generation](https://support.apple.com/en-us/guide/security/seca0c73a75b/web) is a critical component of many cryptographic operations, including key generation, initialization vectors, nonces, and tokens. Apple systems provide a trusted Cryptographically Secure Pseudorandom Number Generator (CSPRNG) that applications should use to ensure the security and unpredictability of generated random values. This CSPRNG is seeded from multiple entropy sources during system startup and over the lifetime of the device. These include the Secure Enclave hardware's True Random Number Generator (TRNG), timing jitter, entropy collected from hardware interrupts, etc.

The kernel CSPRNG is based on the Fortuna design and is exposed to user space via the `/dev/random` and `/dev/urandom` device files, as well as the `getentropy(2)` system call.

Note that on Apple systems like iOS, `/dev/random` and `/dev/urandom` are [equivalent](https://keith.github.io/xcode-man-pages/random.4.html) and both provide cryptographically secure random numbers. However, using these device files directly in application code is discouraged. Instead, developers should use higher-level APIs like `SecRandomCopyBytes` for generating random numbers.

## SecRandomCopyBytes

As part of its Security framework, Apple provides a [Randomization Services](https://developer.apple.com/documentation/security/randomization-services "Randomization Services") API, which generates cryptographically secure random numbers when calling the [`SecRandomCopyBytes`](https://developer.apple.com/documentation/security/secrandomcopybytes(_:_:_:)) function.

In Swift, it is used as follows:

```swift
var randomBytes = [UInt8](repeating: 0, count: 16)
let status = SecRandomCopyBytes(kSecRandomDefault, randomBytes.count, &randomBytes)
```

The `SecRandomCopyBytes` API is the recommended approach for generating cryptographically secure random numbers on iOS. It wraps `CCRandomCopyBytes` from the CommonCrypto library, which in turn uses the system's CSPRNG.

```bash
frida-trace -n 'MASTestApp' -i "SecRandomCopyBytes"
  ...
  2960 ms  SecRandomCopyBytes()
  2960 ms     | CCRandomCopyBytes()
  2960 ms     |    | CCRandomGenerateBytes()
```

## Swift Standard Library

Swift 4.2 introduced a new, native random API through [SE-0202](https://github.com/swiftlang/swift-evolution/blob/main/proposals/0202-random-unification.md), which unified and simplified random number generation within the Swift standard library. This API allows developers to generate random numbers directly from numeric types such as `Int`, `UInt`, `Float`, `Double`, and `Bool` using the [`random(in:)`](https://developer.apple.com/documentation/swift/uint/random(in:)-8zzqt) method, eliminating the need for directly using the `arc4random_uniform()` function in most cases.

```swift
var token = ""
for _ in 0..<16 {
    let b = UInt8.random(in: 0...255)
    token += String(format: "%02x", b)
}
```

Under the hood, the Swift standard library uses [`SystemRandomNumberGenerator`](https://developer.apple.com/documentation/swift/systemrandomnumbergenerator), which leverages platform-specific secure random mechanisms (the system's CSPRNG) and is automatically seeded and thread safe. On Apple platforms, the above methods are implemented under the hood using `arc4random_buf`. You can observe this behavior using Frida to trace calls to the Swift random API. `UInt8.random` can be traced via the mangled symbol using the following pattern:

```bash
frida-trace -n 'MASTestApp' -i "*FixedWidthInteger*random*"
  ...
  2959 ms  $ss17FixedWidthIntegerPsE6random2inxSNyxG_tFZ()
  2959 ms     | arc4random_buf(buf=0x16ef965a8, nbytes=0x8)
```

Therefore, using the Swift standard library's random APIs with the default `SystemRandomNumberGenerator` is generally suitable for cryptographic purposes on Apple platforms because that generator is defined to use a cryptographically secure algorithm backed by the system CSPRNG.

**Note:** The API also offers additional overloads that accept a custom random number generator conforming to the `RandomNumberGenerator` protocol. For example, the previous `UInt8.random(in: 0...255)` is an alias for:

```swift
var rng = SystemRandomNumberGenerator()
let b = UInt8.random(in: 0...255, using: &rng)
```

But developers can implement their own `RandomNumberGenerator`, which may not be secure. Therefore, when using custom generators, ensure they are suitable for cryptographic use cases. See [SE-0202](https://github.com/swiftlang/swift-evolution/blob/main/proposals/0202-random-unification.md) for more details.

## CommonCrypto

The CommonCrypto library provides the `CCRandomGenerateBytes` and `CCRandomCopyBytes` functions for generating cryptographically secure random bytes. While these functions are secure, they are lower-level APIs compared to `SecRandomCopyBytes` and require more careful handling by developers.

## /dev/random

Direct use of `/dev/random` via `open` and `read` is discouraged because it is a low level interface that is easy to misuse from application code. On Apple platforms, `/dev/random` and `/dev/urandom` are backed by the same Fortuna based kernel CSPRNG and behave equivalently, so the usual Linux advice about `/dev/random` blocking when entropy is low does not apply here. For iOS apps, Apple recommends using higher level APIs such as `SecRandomCopyBytes` or the Swift standard library random APIs instead of reading these device files directly.

## arc4random

The `arc4random` family of functions (`arc4random()`, `arc4random_buf()`, `arc4random_uniform()`) is also available on iOS. On modern Apple platforms these functions are backed by the same kernel CSPRNG as `SecRandomCopyBytes`, and are suitable for cryptographic use, but they are legacy C style interfaces and are easier to misuse than the Swift standard library or `SecRandomCopyBytes`. For example:

- Using `arc4random() % n` to generate a bounded value can introduce [modulo bias](https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#Modulo_bias), where some outcomes are slightly more likely than others.
- `arc4random_buf()` produces cryptographically strong random bytes into a caller provided buffer, and does not itself suffer from modulo bias. Any bias only appears if you convert those bytes to a bounded range incorrectly, for example by doing `% n` on derived integers.
- `arc4random_uniform(n)` is specifically designed to avoid modulo bias for arbitrary upper bounds, returning a uniformly distributed integer in the range `[0, n)`, and should be preferred over `arc4random() % n`.

For new Swift code, prefer `UInt8.random(in:)` and related APIs or `SecRandomCopyBytes`, and reserve the `arc4random` family for interoperating with existing C and Objective C code.

## Standard C Library Functions

The standard C library functions `rand()`, `random()`, and their seed setting counterparts `srand()` and `srandom()` are not suitable for cryptographic purposes. Implementations of `rand()` are usually linear congruential generators, and on Apple systems `random()` uses a non linear additive feedback generator, but in all cases these are deterministic pseudorandom generators whose output can be predicted once the internal state or seed is known. See ["Bugs" section of rand(3)](https://www.manpagez.com/man/3/random/) for more details.

These APIs still exist inside `libSystem` at runtime, but the Darwin headers mark them as unavailable to Swift by attaching a Swift availability attribute. In `stdlib.h` the declarations for `rand`, `srand`, `random`, `rand_r` and several `*rand48` functions carry the availability annotation `__swift_unavailable`. So calling them from Swift produces an error that tells you they are unavailable in Swift.

Some of the `*rand48` functions, for example [`drand48()`](https://www.manpagez.com/man/3/drand48/), are still available, but they are also unsuitable for cryptographic purposes and should be avoided. These functions implement a linear congruential generator with a 48-bit state, which is not secure for cryptographic applications. They are also not thread safe and can produce predictable outputs if the seed is known.
