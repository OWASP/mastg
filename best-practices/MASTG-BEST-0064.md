---
title: Use Safe APIs for Object Deserialization
alias: use-safe-apis-for-object-deserialization
id: MASTG-BEST-0064
platform: ios
knowledge: [MASTG-KNOW-0075]
---

Use secure, class-restricted deserialization for object archives that can be influenced by an attacker. This includes archives received from files, IPC payloads, network responses, pasteboard data, app extensions, shared containers, or other storage locations outside the app's full control.

For current Apple guidance, see [Archives and Serialization](https://developer.apple.com/documentation/foundation/archives-and-serialization), [`NSSecureCoding`](https://developer.apple.com/documentation/foundation/nssecurecoding), and [`NSKeyedUnarchiver`](https://developer.apple.com/documentation/foundation/nskeyedunarchiver). Apple's archived Secure Coding Guide section on ["Validating Input and Interprocess Communication"](https://developer.apple.com/library/archive/documentation/Security/Conceptual/SecureCodingGuide/Articles/ValidatingInput.html) still provides useful background and examples.

## Use Secure Coding and Class-Restricted Decoding

Make classes that participate in secure archives conform to [`NSSecureCoding`](https://developer.apple.com/documentation/foundation/nssecurecoding) and return `true` from [`supportsSecureCoding`](https://developer.apple.com/documentation/foundation/nssecurecoding/supportssecurecoding). Decode nested objects with class-restricted APIs such as [`decodeObject(of:forKey:)`](https://developer.apple.com/documentation/foundation/nscoder/decodeobject(of:forkey:)-roif).

Enable secure coding when creating and reading archives. Use [`archivedData(withRootObject:requiringSecureCoding:)`](https://developer.apple.com/documentation/foundation/nskeyedarchiver/archiveddata(withrootobject:requiringsecurecoding:)) with `requiringSecureCoding` set to `true`, and ensure [`requiresSecureCoding`](https://developer.apple.com/documentation/foundation/nskeyedunarchiver/requiressecurecoding) is enabled before decoding attacker-influenced data.

## Restrict Top-Level Unarchiving

Use top-level unarchiving APIs that require the expected class or set of allowed classes, such as [`unarchivedObject(ofClass:from:)`](https://developer.apple.com/documentation/foundation/nskeyedunarchiver/unarchivedobject(ofclass:from:)). When decoding collections, include both the collection type and the allowed element types in the allowed class set.

## Validate Decoded Values

Treat decoded objects as input data. Validate their contents before using them in security-sensitive decisions, file paths, URLs, commands, authorization logic, or persistent application state.
