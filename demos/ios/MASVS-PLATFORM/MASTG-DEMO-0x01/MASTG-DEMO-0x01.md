---
platform: ios
title: Custom URL Scheme Handler Without Input Validation
code: [swift, xml]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
status: draft
---

## Sample

The app registers a custom URL scheme (`mastgtest://`).

The URL handler reads the `amount` query parameter and uses it directly as a string without converting it to a numeric type or checking its value against any bounds. Any app on the device can open `mastgtest://transfer?amount=9999999` and the handler will process the arbitrary value.

{{ Info.plist }}

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the URL handler and check for input validation. Run the r2 script with the `-i` option.

{{ input_validation.r2 # run.sh }}

## Observation

The output shows the `handleURL` method symbol, an empty result for `Int` conversion references, the `onOpenURL` registration, and focused disassembly of the handler covering URL parsing and value extraction.

{{ output.txt }}

## Evaluation

The test case fails because the handler uses the URL parameter value directly without any type conversion or bounds checking. The disassembly reveals the following flow:

- At `0x100004348`, the handler calls `URLComponents.init(url:resolvingAgainstBaseURL:)` to parse the incoming URL.
- At `0x10000434c`, it calls `URL.host` to read the action and compares it against the `"transfer"` string literal loaded at `0x1000043ac`.
- At `0x100004510`, it calls `URLQueryItem.value` to extract the raw `String?` value of the `amount` parameter.
- At `0x100004538`, a `cbz` instruction checks whether the value is nil. If nil, the fallback `"0"` string literal is loaded at `0x100004550`. If not nil, the raw string is used as is.
- At `0x100004594`, the value (either the raw string or the `"0"` fallback) is passed directly to `DefaultStringInterpolation.init` to build the `"Transferring ... units"` output string at `0x1000045a4`.

Between `URLQueryItem.value` (`0x100004510`) and `DefaultStringInterpolation` (`0x100004594`) there is no call to `Int.init` or any other type conversion or validation function. This is further confirmed by the empty "References to Int conversion" section. The handler accepts any arbitrary string value from the URL parameter and uses it directly.
