---
platform: ios
title: Custom URL Scheme Handler Without Input Validation
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: fail
status: draft
---

## Sample

The app registers two custom URL schemes in its `Info.plist` via `CFBundleURLTypes`:

- `mastgtest://` — handled **without** source or parameter validation (FAIL).
- `mastgtest-safe://` — handled **with** source allowlisting and parameter bounds-checking (PASS).

Both are dispatched from the single `application:openURL:options:` delegate method.

{{ MastgTest.swift }}

To trigger the insecure `mastgtest://` handler from a device with the app installed, open the **Notes** app, type the following URL in a new note, and long-press the link to open it:

```text
mastgtest://transfer?amount=9999
```

The app will initiate a transfer of 9999 units without checking which app opened the link or whether the amount is within an acceptable range. Any app on the device can trigger the same action.

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0x01 to inspect the `Info.plist` for registered URL schemes:

    ```bash
    grep -A 5 CFBundleURLSchemes ./Payload/MASTestApp.app/Info.plist
    ```

3. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run the r2 script.

{{ url_scheme_handler.r2 # run.sh }}

The script:

- Searches for `mastgtest` scheme strings in the binary (`izz~mastgtest`).
- Lists flags for `openURL:options` reloc entries (`f~openURL:options`).
- Resolves cross-references to the `application:openURL:options:` implementation (`axt`).
- Checks whether `UIApplicationOpenURLOptionsSourceApplicationKey` appears as a flag (`f~UIApplicationOpenURLOptionsSourceApplicationKey`).
- Disassembles the handler function (`pdf`).

## Observation

The output shows two registered URL scheme strings (`mastgtest` and `mastgtest-safe`), the relocation entry for `application:openURL:options:`, a cross-reference to its implementation, a reference to the `sourceApplication` key, and the full disassembly of the handler function.

{{ output.txt }}

## Evaluation

The test fails because the `mastgtest://` branch of the handler processes the URL without reading `UIApplicationOpenURLOptionsSourceApplicationKey` from the `options` dictionary and without validating the `amount` parameter.

### Info.plist Analysis

The `CFBundleURLSchemes` array in `Info.plist` declares both `mastgtest` and `mastgtest-safe`. Any app on the device can open either scheme by calling `openURL:options:completionHandler:`.

### Binary Analysis

**Locating the handler:**

The r2 output shows the relocation entry at `0x100010ab0` for `reloc.fixup.application:openURL:options:`, and `axt` confirms the implementation is at `sym.MASTestApp.AppDelegate.application_open_options__1` (called at `0x100005210`).

**`mastgtest://` branch — no source validation (FAIL):**

In the disassembly, after the scheme comparison at `0x100005068–0x100005074`, the `mastgtest` branch loads URL components and accesses `url.host` and `queryItems` directly. The `options` dictionary argument (held in `x20`) is **never dereferenced** in this branch — there is no call to retrieve `UIApplicationOpenURLOptionsSourceApplicationKey` and no comparison against an allowlist. The `amount` query parameter is used as a raw string without any bounds-checking before the transfer string is constructed:

```text
; NOTE: UIApplicationOpenURLOptionsSourceApplicationKey is NOT read here
; The options dictionary (x20) is never accessed in this branch
0x1000050b0      ; ... (find amount, build string, return true)
```

**`mastgtest-safe://` branch — source validation present (PASS):**

After the `mastgtest-safe` scheme comparison (`0x1000051c4–0x1000051d0`), the disassembly shows:

- `0x1000051d4` — loads `reloc.fixup.UIApplication.OpenURLOptionsKey.sourceApplication.fget` and calls the dictionary subscript getter to read the source application from `x20` (the `options` dictionary).
- `0x1000051f8` — calls `swift_stdlib_Set_contains__1` to check the result against the `allowedSources` set.
- `0x100005280` — returns `false` if the source is not in the allowlist, aborting the operation before any transfer takes place.
- Subsequent code validates the `amount` parameter bounds before performing the transfer.

The contrast between the two branches is visible at the assembly level: only the `mastgtest-safe://` branch references `UIApplicationOpenURLOptionsSourceApplicationKey` (`0x100010bc8` in the flag table), confirming that the insecure `mastgtest://` handler does not perform source verification.
