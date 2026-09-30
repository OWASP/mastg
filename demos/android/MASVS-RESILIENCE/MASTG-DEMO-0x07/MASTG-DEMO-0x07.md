---
platform: android
title: Retrieving Device Integrity Signals via Key Attestation
id: MASTG-DEMO-0x07
code: [kotlin]
test: MASTG-TEST-0x05
kind: pass
---

## Sample

This sample generates an EC key pair in the Android KeyStore with an attestation challenge, retrieves the resulting certificate chain, and reads the Android key attestation extension (OID `1.3.6.1.4.1.11129.2.1.17`) from the leaf certificate. That extension is where the device integrity signals live: `rootOfTrust` (`verifiedBootState`, `verifiedBootKey`, `deviceLocked`) and `attestationSecurityLevel` (@MASTG-KNOW-0120).

The sample reports that the signals are present and forwards the chain to the server. It deliberately does not evaluate them on the client, because a verdict computed on the device is a bypassable local check. Evaluation belongs on the server, as described in @MASTG-BEST-0x01.

Unlike @MASTG-DEMO-0x03, which shows the challenge being embedded so the chain is fresh, this sample shows the retrieval of the device integrity signals themselves.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 against the reversed Java code.

{{ ../../../../rules/mastg-android-device-attestation-apis.yml }}

{{ run.sh }}

## Observation

The rule identifies three locations: the certificate chain retrieval, the attestation extension OID constant, and the call that reads that extension from the leaf certificate.

{{ output.txt }}

## Evaluation

The test passes because the app retrieves a certificate chain and reads the attestation extension that carries the device integrity signals, giving the backend cryptographic evidence about the device rather than a client-side claim.

Running the sample on an emulator produces:

```txt
Chain length: 3
Attestation extension (1.3.6.1.4.1.11129.2.1.17): present, 338 bytes
Key security level: Software
```

The chain and the extension are present, but `Key security level: Software` means the attestation was produced entirely by the Android OS with no hardware involvement. On such a device the `rootOfTrust` values appear in the `softwareEnforced` list, where the same software layer that is being vouched for asserts them, so they cannot corroborate the device's state. A server must therefore reject this attestation as evidence of device integrity even though every field is present and well-formed.

!!! warning
    A passing static result only confirms that the app collects the device integrity signals. It says nothing about whether the server verifies the chain up to the Google Hardware Attestation Root Certificate, checks the revocation status list, enforces certificate validity, and requires `attestationSecurityLevel` to be `TrustedEnvironment` or `StrongBox`. See @MASTG-BEST-0x01.

!!! note
    These signals reflect the state of the device **at the time the key was generated**, not at the time of use. A key generated on a clean device retains its attestation even if the device is later rooted or its bootloader unlocked, which is why @MASTG-BEST-0x01 calls for fresh key generation at critical moments. See @MASTG-KNOW-0120.
