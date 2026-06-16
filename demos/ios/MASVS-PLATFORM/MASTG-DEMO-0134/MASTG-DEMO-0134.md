---
platform: ios
title: Custom URL Scheme Handler Without Input Validation
code: [swift, xml]
id: MASTG-DEMO-0134
test: MASTG-TEST-0370
kind: fail
---

## Sample

The app registers a custom URL scheme (`mastgtest://`).

The URL handler reads a query parameter value from URLs targeting the `transfer` action and uses it directly as a string without converting it to a numeric type or checking its value against any bounds. Any app on the device can open a URL such as `mastgtest://transfer?amount=9999999`, and the handler will accept the supplied value and use it in the transfer flow without validating that it is a valid, bounded amount.

{{ Info.plist # MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the URL handler and check for input validation. Run the r2 script with the `-i` option.

{{ input_validation.r2 # run.sh }}

## Observation

The output shows the `handleURL` method symbol, an empty result for `Int` conversion references, the `onOpenURL` registration, and focused disassembly of the handler covering URL parsing and query value extraction.

{{ output.txt }}

## Evaluation

The test case fails because the handler uses the URL query value directly without any type conversion or bounds checking. The disassembly reveals the following flow:

- At `0x100004348`, the handler calls `URLComponents.init(url:resolvingAgainstBaseURL:)` to parse the incoming URL.
- At `0x10000434c`, it calls `URL.host` to read the URL host, which represents the action, and compares it against the `"transfer"` string literal loaded at `0x1000043ac`.
- At `0x100004510`, it calls `URLQueryItem.value` to extract the raw `String?` value from a query item.
- At `0x100004538`, a `cbz` instruction checks whether the value is `nil`. If `nil`, the fallback `"0"` string literal is loaded at `0x100004550`. If non `nil`, the raw string value is used as is.
- At `0x100004594`, the value (either the raw string or the `"0"` fallback) is passed directly to `DefaultStringInterpolation.init` to build the `"Transferring ... units"` output string at `0x1000045a4`.

Between `URLQueryItem.value` (`0x100004510`) and `DefaultStringInterpolation` (`0x100004594`) there is no call to `Int.init` or any other visible type conversion or validation function. This is further confirmed by the empty `=== References to Int conversion (input validation) ===` section. The handler therefore accepts an arbitrary string value from the URL query and uses it directly in the transfer related output, instead of validating that the value is numeric and within an expected range.

### Exploitation

You can use @MASTG-TOOL-0072 to launch the app on a connected iOS device with an arbitrary custom URL scheme payload.

First, list the connected devices and copy the device identifier:

```bash
xcrun devicectl list devices
```

Then launch the app with a crafted `mastgtest://` URL:

```bash
xcrun devicectl device process launch \
  --device <DEVICE_IDENTIFIER> \
  --payload-url "mastgtest://transfer?amount=9999999" \
  org.owasp.mastestapp.MASTestApp-iOS
```

After the app opens, tap **Start** in the demo app to process the stored URL. The result is observable in the app output:

```text
Transferring 9999999 units
```

Repeat the command with a non-numeric value:

```bash
xcrun devicectl device process launch \
  --device <DEVICE_IDENTIFIER> \
  --payload-url "mastgtest://transfer?amount=not-a-number" \
  org.owasp.mastestapp.MASTestApp-iOS
```

After tapping **Start**, the app output shows:

```text
Transferring not-a-number units
```

This confirms at runtime that the handler accepts attacker-controlled URL input and uses it directly in the transfer output without numeric conversion or bounds checking.
