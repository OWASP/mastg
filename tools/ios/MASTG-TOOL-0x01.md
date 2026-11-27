---
title: IPA-Decrypt
platform: ios
source: https://github.com/Re13orn/IPA-Decrypt
hosts: [macOS]
alternatives: [MASTG-TOOL-0050]
---

IPA-Decrypt is a GUI tool that downloads an IPA from the App Store or TestFlight and decrypts it offline, without relying on a running process.

Tools such as @MASTG-TOOL-0050 dump decrypted pages out of a running app and therefore need a jailbroken device. IPA-Decrypt instead downloads the original package first and decrypts a local copy, keeping the untouched IPA for comparison. Credentials are validated against the real license before a result is reported as decrypted, and credentials from a jailbroken device can be extracted and reused over USB or SSH.

On supported Apple Silicon Macs the tool can recover a FairPlay model locally from a Mac App Store iPhone or iPad app, so no jailbroken device is involved at all.

!!! note "Requirements and caveats"
    Decryption runs on macOS 15+ on Apple Silicon, or against a jailbroken device for credential extraction. Mac FairPlay models are a different format from the 16-byte `credential.bin` used on iOS and cannot be transferred between the two. TestFlight uses a non-public protocol and may break with Apple-side changes. The decrypted output is unsigned and must be signed again before installation.

## Installation

macOS build, via the author's Homebrew tap:

```bash
brew install --cask re13orn/tap/ipa-decrypt
```

The iOS build is an unsigned IPA on the [releases page](https://github.com/Re13orn/IPA-Decrypt/releases).
