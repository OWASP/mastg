---
platform: ios
title: Universal Link Handler Without Input Validation
code: [swift, xml]
id: MASTG-DEMO-0153
test: MASTG-TEST-0395
kind: fail
---

## Sample

The app declares the Associated Domains entitlement for `applinks:demo.mas.owasp.org`, so iOS routes verified universal links for that domain to the app and delivers them through `onContinueUserActivity(NSUserActivityTypeBrowsingWeb)`.

The handler reads the `amount` query parameter from the universal link's `webpageURL` for the `/transfer` path and uses it directly as a string, without converting it to a numeric type or checking its value against any bounds. The domain is verified by the OS, but the path and query parameters are caller-controlled: anyone can send the user a link such as `https://demo.mas.owasp.org/transfer?amount=9999999`, and the handler will accept the supplied value and use it in the transfer flow without validating that it is a valid, bounded amount.

{{ MASTestApp.entitlements # MASTestAppApp.swift # MastgTest.swift }}

Because the OS only routes the link after verifying the domain, triggering a real universal link requires the Apple App Site Association file to be reachable for `demo.mas.owasp.org`, which isn't the case for this demo. But you can still exercise the same handler by constructing an `NSUserActivity` with a crafted `webpageURL` and invoking the continuation entry point with @MASTG-TOOL-0039, as described in @MASTG-TECH-0169. Either way, the handler returns the attacker-controlled value unchanged:

```text
Transferring 9999999 units
```

Repeating with a non-numeric value (`amount=not-a-number`) returns `Transferring not-a-number units`, confirming at runtime that the handler accepts arbitrary universal link input without numeric conversion or bounds checking.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0111 with @MASTG-TOOL-0129 (`rabin2 -OC`) to extract the entitlements embedded in the signed binary and confirm the app declares universal link support via `com.apple.developer.associated-domains`.
3. Use @MASTG-TECH-0066 to locate the universal link handler and check for input validation. Run the r2 script with the `-i` option.

{{ input_validation.r2 # run.sh }}

## Observation

The extracted entitlements confirm the app is associated with the `demo.mas.owasp.org` domain, so iOS routes its verified universal links to the app:

{{ entitlements_reversed.plist }}

The r2 output shows the `handleUniversalLink` method symbol, an empty result for `Int` conversion references, the `onContinueUserActivity` registration, the `webpageURL` reference, and focused disassembly of the handler covering URL parsing, path comparison, and query value extraction:

{{ output.txt }}

## Evaluation

The test case fails because the handler uses the universal link's query value directly without any type conversion or bounds checking. The disassembly reveals the following flow:

- At `0x1000069ac`, the handler calls `URLComponents.init(url:resolvingAgainstBaseURL:)` to parse the verified universal link URL read from `webpageURL`.
- At `0x100006a44`, it calls `URL.path` to read the URL path, which represents the action, and compares it against the `"/transfer"` string literal loaded at `0x100006a5c`.
- At `0x100006bc4`, it calls `URLQueryItem.value` to extract the raw `String?` value of the `amount` query item.
- At `0x100006cd0`, the value flows into `DefaultStringInterpolation` to build the `"Transferring ... units"` output string, with the `"Transferring"` literal loaded at `0x100006cec`.

Between `URLQueryItem.value` (`0x100006bc4`) and `DefaultStringInterpolation` (`0x100006cd0`) there is no call to `Int.init` or any other visible type conversion or validation function. This is further confirmed by the empty `=== References to Int conversion (input validation) ===` section. The handler therefore accepts an arbitrary string value from the universal link query and uses it directly in the transfer-related output, instead of validating that the value is numeric and within an expected range.
