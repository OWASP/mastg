---
title: Broken Symmetric Encryption Modes
platform: ios
id: MASTG-TEST-0317
type: [static, dynamic]
weakness: MASWE-0020
best-practices: [MASTG-BEST-0005]
profiles: [L1, L2]
---

## Overview

This test focuses on broken symmetric encryption modes such as [ECB (Electronic Codebook)](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Electronic_codebook_(ECB)).

> For more information, please refer to [use of broken encryption modes](../../../Document/0x04g-Testing-Cryptography.md#broken-block-cipher-modes).

In iOS development, the more recent `CryptoKit` does not support ECB mode and is therefore not vulnerable to this issue, as it only supports secure encryption modes like AES-GCM and ChaCha20-Poly1305. However, applications may use the older `CommonCrypto` library or other third-party libraries that may support ECB mode. In this case, it is essential to verify that ECB mode is not being used.

In [`CommonCrypto`](https://web.archive.org/web/20240606000307/https://opensource.apple.com/source/CommonCrypto/CommonCrypto-36064/CommonCrypto/CommonCryptor.h), ECB mode can be enabled by setting the `kCCOptionECBMode` (value `0x0002` or `2`) in the `options` parameter of the `CCCrypt` function. When `kCCOptionECBMode` is set in the options parameter, the encryption uses ECB mode, which is considered vulnerable. The default behavior (when `kCCOptionECBMode` is not set) is to use CBC mode, which is more secure when used with a proper initialization vector (IV).

## Steps

1. Use @MASTG-TECH-0065 to reverse engineer the app.
2. Use @MASTG-TECH-0072 to look for uses of symmetric encryption and its modes.
3. Use @MASTG-TECH-0076 to analyze the relevant code paths and obtain the values for the encryption modes.

## Observation

The output should contain the uses of symmetric encryption and its modes.

## Evaluation

The test case fails if you can find the use of symmetric encryption with broken modes enabled, such as ECB mode.
