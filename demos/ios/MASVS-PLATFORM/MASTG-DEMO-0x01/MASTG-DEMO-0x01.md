---
platform: ios
title: App Extension Caching a Secret in the Shared Container Instead of the Shared Keychain
id: MASTG-DEMO-0x01
code: [swift]
test: MASTG-TEST-0x01
kind: fail
---

### Sample

The app and its Share Extension share an App Group (`group.org.owasp.mastestapp`) and a Keychain Access Group.

The **main app** does it correctly: it stores the auth token in the **shared Keychain**, the appropriate channel for a secret shared with the app's extensions.

{{ MastgTest.swift }}

The **Share Extension** reads the token from the shared Keychain (the correct source), but then caches it **unencrypted in the App Group shared container** (both in shared `UserDefaults` and in a file), exposing it to every member of the App Group.

{{ ShareViewController.swift }}

Both the app and the extension declare the App Group and the Keychain Access Group:

{{ MASTestApp.entitlements # ShareExtension_Info.plist # ShareExtension.entitlements }}

### Steps

We read the entitlements of the app and the extension, then analyze each binary with @MASTG-TOOL-0073. The binaries are extracted from the built IPA (@MASTG-TECH-0058): the app at `Payload/MASTestApp.app/MASTestApp` and the extension at `Payload/MASTestApp.app/PlugIns/ShareExtension.appex/ShareExtension`.

{{ app_keychain.r2 # extension_shared_storage.r2 # run.sh }}

### Observation

{{ output.txt }}

The analysis contrasts the two binaries:

- The **main app** references the Keychain APIs (`SecItemAdd`, `kSecAttrAccessGroup`) and contains neither the App Group identifier nor the shared-container write APIs.
- The **Share Extension** contains the App Group identifier and the shared-container write APIs (`containerURLForSecurityApplicationGroupIdentifier:` for the file container and `setObject:forKey:` for the shared `UserDefaults`), while it reads the token from the shared Keychain with `SecItemCopyMatching`.

!!! note
    The keys the extension writes (`cachedAuthToken`, `auth_cache.json`) are 15-character Swift _small strings_, which the Release compiler stores inline as immediate values rather than in the `__cstring` section, so they do not appear in a string search. The App Group identifier (26 characters) is too long for that optimization and remains visible, as do the ObjC selectors and the imported `Sec*` functions.

### Evaluation

The test case fails because of the **Share Extension**: it caches the auth token (a secret) in the App Group shared container without protection.

- It writes the token to the shared `UserDefaults` (the `setObject:forKey:` selector on the App Group suite), in plaintext. Any member of the App Group can read it.
- It writes the token to a file in the shared container (`containerURLForSecurityApplicationGroupIdentifier:`), in plaintext and without `NSFileProtectionComplete`.

The main app shows the correct pattern: the secret belongs in the shared Keychain (scoped to the `keychain-access-groups` entitlement), which the extension can read with `SecItemCopyMatching` whenever it needs the token, instead of copying it into the shared container. As described in @MASTG-BEST-0x01, prefer a shared Keychain for credentials and tokens, and protect any sensitive data kept in the shared container with encryption and `NSFileProtectionComplete`.
