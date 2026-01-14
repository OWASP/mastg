---
platform: ios
title: Uses of Broken Encryption Modes in CommonCrypto with r2
code: [swift]
id: MASTG-DEMO-0080
test: MASTG-TEST-0317
---

### Sample

The snippet below shows sample code that uses the insecure ECB (Electronic Codebook) mode when encrypting data with CommonCrypto's `CCCrypt` function. ECB mode is vulnerable because it encrypts identical plaintext blocks to identical ciphertext blocks, revealing patterns in the data.

{{ MastgTest.swift # function.asm }}

### Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ cccrypt-ecb.r2 }}

{{ run.sh }}

### Observation

The output contains the disassembled code for the `CCCrypt` function in ECB mode.

{{ output.asm }}

### Evaluation

Inspect the disassembled code to identify the use of insecure encryption modes.

In [CommonCryptor.h](https://github.com/xybp888/iOS-SDKs/blob/master/iPhoneOS18.6.sdk/usr/include/CommonCrypto/CommonCryptor.h), you can find the definition of the `CCCrypt` function:

```c
CCCryptorStatus CCCrypt(
    CCOperation op,         /* kCCEncrypt, etc. */
    CCAlgorithm alg,        /* kCCAlgorithmAES128, etc. */
    CCOptions options,      /* kCCOptionPKCS7Padding, kCCOptionECBMode, etc. */
    const void *key,
    size_t keyLength,
    const void *iv,         /* optional initialization vector */
    const void *dataIn,     /* optional per op and alg */
    size_t dataInLength,
    void *dataOut,          /* data RETURNED here */
    size_t dataOutAvailable,
    size_t *dataOutMoved);
```

There you will also find the `options` parameter, which can include `kCCOptionECBMode`:

```c
/*!
    @enum        CCOptions
    @abstract    Options flags, passed to CCCryptorCreate().

    @constant    kCCOptionPKCS7Padding   Perform PKCS7 padding.
    @constant    kCCOptionECBMode        Electronic Code Book Mode.
                                         Default is CBC.
*/
enum {
    kCCOptionPKCS7Padding   = 0x0001,
    kCCOptionECBMode        = 0x0002,
};
typedef uint32_t CCOptions;
```

In the disassembly, the third argument to `CCCrypt` is passed in `w2` and is set to the constant value `3`. This argument corresponds to the `CCOptions` parameter, which is defined as a bitmask rather than a single enumerated value. That distinction is important because it means individual options are combined by setting bits, not by selecting one value from a list.

When analyzing a bitmask, the numeric value must be decomposed into its constituent flags. The value `3` in binary is `0b11`. The least significant bit, `0b01`, corresponds to `kCCOptionPKCS7Padding`, which has a value of `1`. The next bit, `0b10`, corresponds to `kCCOptionECBMode`, which has a value of `2`. Since both of these bits are set in `3`, both options are enabled at the same time.

Looking at the rest of the arguments confirms the interpretation. `w0` is `0`, mapping to `kCCEncrypt`, and `w1` is `0`, mapping to `kCCAlgorithmAES128`. The key length passed in `w4` is `0x10`, indicating a 16-byte key, which is appropriate for AES 128. The IV argument is passed as a null pointer, which is consistent with ECB mode, since ECB does not use an initialization vector.

Taken together, we can conclude that **the test fails** because the app is performing AES-128 encryption with PKCS7 padding enabled and in ECB mode.
