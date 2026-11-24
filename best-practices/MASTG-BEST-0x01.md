---
title: Use Strong Hash Functions for Security-Relevant Data
alias: use-strong-hash-functions-android
id: MASTG-BEST-0x01
platform: android
knowledge: [MASTG-KNOW-0x01]
---

Use a current hash function whenever collision resistance or cryptographic integrity matters. Android's [cryptography guidance](https://developer.android.com/privacy-and-security/cryptography) recommends the SHA-2 family, such as SHA-256, for `MessageDigest`. Do not use MD5 or SHA-1 for security-relevant hashing; Android classifies both as [weak or broken cryptographic hash functions](https://developer.android.com/privacy-and-security/risks/broken-cryptographic-algorithm).

A plain message digest does not authenticate its input. Use an HMAC with a SHA-2 family hash when integrity must be verified with a shared secret, or a digital signature when verification uses a public key.

Do not use a fast general-purpose hash such as SHA-256 directly for password storage. Use a salted password hashing function with an appropriate work factor, following the [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html).

For non-security checksums, document that collision resistance and authenticity are not required. This context lets reviewers distinguish an ordinary compatibility checksum from a digest used as a security control.
