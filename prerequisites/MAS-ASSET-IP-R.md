---
id: MAS-ASSET-IP-R
title: Intellectual Property Data at Rest
---

## MAS-ASSET-IP-R: Intellectual Property Data at Rest

This asset category covers intellectual property data that is stored persistently on a device. Intellectual property data at rest includes any technical or proprietary information the application stores in files, databases, or other data stores.

### Examples

- API keys, secrets, or certificates stored in application files, shared preferences, or keychain/keystore entries used to authenticate the application itself to its own backends
- API keys, secrets, or certificates stored locally that the application uses to authenticate itself to third-party services (e.g., Crashlytics, Google Maps)
- Encryption keys or key material persisted on the device that are used to protect other data or the system itself
- Hardcoded credentials embedded in the application binary or configuration files

### Risk

An adversary who gains access to intellectual property data at rest may be able to:

- Impersonate the application to backend services
- Bypass access controls or payment mechanisms
- Escalate privileges on the application's infrastructure
- Abuse third-party service quotas or capabilities
