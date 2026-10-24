---
platform: ios
title: References to Object Deserialization of a URL Scheme Payload with r2
code: [swift, xml]
id: MASTG-DEMO-0149
test: MASTG-TEST-0386
status: draft
---

## Sample

The sample imports a user session that arrives in a custom URL scheme (`mastgtest://import?session=<base64 archive>`), which an attacker can deliver from Safari, Notes, or another app. `Info.plist` registers the `mastgtest` scheme and `MASTestAppApp.swift` forwards the opened URL to `mastgTest()`. For example:

```bash
mastgtest://import?session=YnBsaXN0MDDUAQIDBAUGBwpYJHZlcnNpb25ZJGFyY2hpdmVyVCR0b3BYJG9iamVjdHMSAAGGoF8QD05TS2V5ZWRBcmNoaXZlctEICVRyb290gAGlCwwTFBVVJG51bGzTDQ4PEBESWGNvbnRlbnRzViRjbGFzc1hmaWxlTmFtZYADgASAAl8QE3B3bmVkX3ZpYV9saW5rLmh0bWxfEG08IWRvY3R5cGUgaHRtbD48aHRtbD48Ym9keT48aDE%2BUHduZWQgdmlhIGRlZXAgbGluazwvaDE%2BPHNjcmlwdD5hbGVydCgicHduZWQgdmlhIGxpbmsiKTwvc2NyaXB0PjwvYm9keT48L2h0bWw%2B0hYXGBlaJGNsYXNzbmFtZVgkY2xhc3Nlc15DYWNoZWREb2N1bWVudKIaG15DYWNoZWREb2N1bWVudFhOU09iamVjdAAIABEAGgAkACkAMgA3AEkATABRAFMAWQBfAGYAbwB2AH8AgQCDAIUAmwELARABGwEkATMBNgFFAAAAAAAAAgEAAAAAAAAAHAAAAAAAAAAAAAAAAAAAAU4%3D
```

The payload was generated using `PayloadGenerator.swift`. It builds a `CachedDocument` archive with an attacker-chosen file name and contents, then base64- and URL-encodes it into a `mastgtest://import?session=...` link.

{{ PayloadGenerator.swift }}

You can regenerate the payload using the following command, which prints the complete link as shown above.

```bash
swift PayloadGenerator.swift
```

To test it with the app running on a device, follow @MASTG-TECH-0169. Once you trigger the URL scheme and the app opens, click on **Start** so the app processes the link.

The payload is deserialized through both an insecure and a secure path:

- The insecure path defines `InsecureUserSession`, which conforms to `NSCoding` instead of `NSSecureCoding`. The imported archive is decoded with `requiresSecureCoding = false` and `decodeObject(forKey:)`, so a substituted archive is decoded without type enforcement.

- The secure path defines `SecureUserSession`, which conforms to `NSSecureCoding`, returns `true` from `supportsSecureCoding`, decodes nested objects with `decodeObject(of:forKey:)`, and reads the top-level object with `unarchivedObject(ofClass:from:)`, which rejects a substituted archive.

The sample also includes `CachedDocument`, a plausible offline-cache model whose `init(coder:)` writes a cached file to disk. Because the insecure path doesn't restrict the decoded class, an attacker who substitutes `CachedDocument` into the link's payload turns that normal cache restore into an attacker-controlled file write that runs during decoding, while the secure path rejects the class before it is instantiated.

{{ MastgTest.swift # Info.plist # MASTestAppApp.swift }}

## Steps

1. Unzip the app package and locate the main binary file using @MASTG-TECH-0058. In this case, the binary is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh`.

{{ nscoding.r2 # run.sh }}

## Observation

The `=== Xrefs to decoding APIs ===` section of `output.txt` shows that a single function disables secure coding and decodes without a class restriction: `MastgTest.decodeInsecurely` references both `setRequiresSecureCoding:` (at `0x000070c8`) and the unrestricted `decodeObjectForKey:` (at `0x00007130`).

The `=== Focused proof snippets ===` section of `output.txt` disassembles those two call sites (the full function is in `decodeInsecurely.asm`):

- At `0x000070c8` the `setRequiresSecureCoding:` selector is loaded, the value `0` is prepared at `0x000070cc` to `0x000070d0`, and `objc_msgSend` is called at `0x000070d4`.
- The same function calls `decodeObjectForKey:` at `0x00007130` to `0x00007134`.

{{ output.txt # decodeInsecurely.asm # importSharedSession.asm }}

## Evaluation

The test fails because the app disables secure coding by passing `0` to `setRequiresSecureCoding:`. It then calls `decodeObjectForKey:` without an expected class or allowed class list. And, all of this is reachable from the `mastgtest` custom URL scheme import path, which an attacker can trigger with a crafted link.

Further reverse engineering shows how the vulnerable decode is reachable from a custom URL scheme payload:

```mermaid
flowchart TD
    A["mastgtest://import?session=BASE64"] --> B["importSharedSession, 0x00006644"]
    B --> C["Base64 decode, 0x000069f4 to 0x00006a0c"]
    C --> D["decodeInsecurely, 0x00007040"]
    D --> E["setRequiresSecureCoding false, 0x000070c8 to 0x000070d4"]
    E --> F["decodeObjectForKey, 0x00007130 to 0x00007134"]
    F --> G["Class named in archive is instantiated"]
```

The `=== Reachability from URL payload ===` section of `output.txt` traces `MastgTest.importSharedSession(from:)` (function at `0x00006644`, full function in `importSharedSession.asm`):

- It parses URL components, reads the `session` query item, Base64-decodes the value at `0x000069f4` to `0x00006a0c`.
- Then, it passes the resulting data to `MastgTest.decodeInsecurely` at `0x00006d20`.

The `=== Consequence path ===` section of `output.txt` follows what a substituted `CachedDocument` does once instantiated:

- Its initializer decodes `fileName` at `0x00005644` and `contents` at `0x00005818`.
- Then, it calls `restoreToDisk` at `0x00005a78` (function at `0x00005b14`), which builds a file path with `appendingPathComponent` and writes the file at `0x00005e10`.

```mermaid
flowchart TD
    A["Substituted class from archive"] --> B["init(coder:), 0x00005644"]
    B --> C["decode fileName and contents"]
    C --> D["restoreToDisk, 0x00005b14"]
    D --> E["write call, 0x00005e10"]
```

The secure decoding path is the contrast. The same URL import function also calls a separate secure decoder at `0x00006dc0`. In that path, class restricted unarchiving is used for the expected session class, so a substituted archive is rejected before the substituted class initializer can run.
