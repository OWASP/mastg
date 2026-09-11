---
platform: ios
title: Custom URL Scheme Handler with Source Validation
code: [swift, xml]
id: MASTG-DEMO-0135
test: MASTG-TEST-0371
kind: pass
---

## Sample

The app registers a custom URL scheme (`mastgtest://`).

The `SceneDelegate` handles incoming URLs via `scene(_:openURLContexts:)`. For each URL, the handler reads `sourceApplication` from `UIOpenURLContext.options` and checks it against a hardcoded `allowedSources` set before processing. In this demo we verify that the `sourceApplication` property is accessed in the compiled binary.

Apple only populates `sourceApplication` when the calling app belongs to the same Apple Developer Team. Apps from other teams or system apps (e.g. Notes, Safari) will have `sourceApplication` set to `nil`. This is an Apple platform limitation, but it still allows verifying that the URL was opened by one of your own apps, which is useful when a URL scheme triggers privileged actions that should only be accessible from within your app suite.

{{ Info.plist # MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the URL handler and check for source validation references. Run the r2 script with the `-i` option.

{{ url_scheme_handler.r2 # run.sh }}

## Observation

The output shows the `SceneDelegate`'s `scene:openURLContexts:` handler, the cross-references to `sourceApplication`, and the disassembly around each access site.

{{ output.txt }}

## Evaluation

The test case passes because `sourceApplication` is accessed from both URL handler paths: `willConnectTo`, the cold launch path where iOS starts the app to handle the URL, at `0x100004c8c`, and `openURLContexts`, the warm open path where the app is already running or suspended, at `0x1000051a8`.

In each disassembly block:

- `ldr x1, ... reloc.fixup.sourceApplication` loads the `sourceApplication` selector.
- `bl sym.imp.objc_msgSend` sends it to the URL options object, retrieving the source application value.
- The returned Objective C object is retained and bridged into a Swift `String` when non `nil`.
- `cbz x20, ...` branches when the result is `nil`, which means the source application value was unavailable or not provided. For cross-app opens, Apple documents this as the expected result when the originating app has a different team identifier.

This confirms that the handler reads the caller application identifier on both URL entry paths. Together with the surrounding source validation logic in the sample, this demonstrates that the custom URL scheme handler checks the caller source before processing the URL.

### Exploitation

You can use @MASTG-TOOL-0072 to launch the app on a connected iOS device with an arbitrary custom URL scheme payload and confirm that the handler rejects the request when `sourceApplication` is not populated with an allowlisted bundle ID.

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

After the app opens, tap **Start** in the demo app to process the stored event. Because the URL was not opened by an allowlisted same team app, the result is observable in the app output:

```text
Incoming URL: mastgtest://transfer?amount=9999999
Source app:   (none)
Handler returned: false
```

This confirms at runtime that the URL handler reads `sourceApplication` and only returns `true` when the source application bundle ID matches the allowlist.
