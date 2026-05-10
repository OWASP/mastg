---
id: MAS-ASSET-IP-U
title: Intellectual Property Data in Use
---

## MAS-ASSET-IP-U: Intellectual Property Data in Use

This asset category covers intellectual property data that is loaded into an application's memory during runtime. Intellectual property data in use includes any technical or proprietary information the application decrypts, processes, or holds in its address space.

### Examples

- API keys, secrets, or certificates decrypted or loaded into memory to authenticate the application to its own backends
- API keys, secrets, or certificates held in memory during calls to third-party services (e.g., Crashlytics, Google Maps)
- Encryption keys or key material present in memory while data is being encrypted or decrypted
- Proprietary algorithm parameters or model weights loaded into the application's address space at runtime