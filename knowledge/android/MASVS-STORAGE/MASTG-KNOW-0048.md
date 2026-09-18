---
masvs_category: MASVS-STORAGE
platform: android
title: KeyChain
---

The [`KeyChain`](https://developer.android.com/reference/android/security/KeyChain) class (`android.security.KeyChain`) provides system-wide access to private keys and their corresponding certificate chains stored in the device's credential storage. It is a shared credential store. Apps request keys and certificates from it. Access is controlled separately for each credential.

The most common use case is client certificate authentication. The app presents a certificate and proves that it holds the matching private key. This is used in mutual TLS (mTLS), VPN, and Wi-Fi EAP authentication.
The store is system-wide, but an app does not gain access to its contents simply because credentials exist on the device.

## Accessing keys and certificates

Credentials enter the store in two ways. They are imported through the `Intent` returned by `createInstallIntent()`, or they are provisioned by device policy.

An app receives a credential after an `X509KeyManager` callback requests one. `choosePrivateKeyAlias()` presents a system UI where the user selects an available key. `getPrivateKey()` and `getCertificateChain()` then return the credential.

An alias is the string identifier that names a credential. The app receives this alias and may reuse it. Remembering a chosen alias lets the app skip the selection prompt on later connections. A lookup with an invalid alias returns `null`.

`getPrivateKey()` returns a `PrivateKey` handle. When a key is bound to secure hardware, the raw key material is not exposed to the app. The system performs the cryptographic operations on the app's behalf.

## Access control

Access to a given key is granted in one of three ways: user selection, device policy, or a credential management app.

With user selection, `choosePrivateKeyAlias()` grants the app access to a specific alias through a system UI.

With device policy, a Device Owner or Profile Owner grants the app access to a key pair through [`DevicePolicyManager`](https://developer.android.com/reference/android/app/admin/DevicePolicyManager), for example with `grantKeyPairToApp()`. No user interaction is involved.

A credential management app is a single app that holds the credential management app role. Since Android 12 (API level 31), this app can manage KeyChain credentials on the user's behalf.

## Hardware-backed keys

A private key from the KeyChain may be backed by secure hardware, or it may be implemented in software. Secure hardware here means a Trusted Execution Environment (TEE) or a StrongBox secure element.

A [`KeyInfo`](https://developer.android.com/reference/android/security/keystore/KeyInfo) instance exposes the key's security level. It is obtained through `KeyFactory` and `getKeySpec()`. `KeyInfo.getSecurityLevel()` reports this level on API level 31 and above. It replaces `KeyInfo.isInsideSecureHardware()`, which was deprecated in API level 31.

When a key is backed by secure hardware, the key material does not leave the secure environment (see the [Android Keystore system](https://developer.android.com/privacy-and-security/keystore)).
