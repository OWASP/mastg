---
platform: ios
title: Custom URL Scheme Handler with Source Validation
code: [swift, xml]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: pass
status: draft
---

## Sample

The app registers a custom URL scheme (`mastgtest://`).

The `SceneDelegate` handles incoming URLs via `scene(_:openURLContexts:)`. For each URL, the handler reads `sourceApplication` from `UIOpenURLContext.options` and checks it against a hardcoded `allowedSources` set before processing. In this demo we verify that the `sourceApplication` property is actually accessed in the compiled binary.

Apple only populates `sourceApplication` when the calling app belongs to the same Apple Developer Team. Apps from other teams or system apps (e.g. Notes, Safari) will have `sourceApplication` set to `nil`. This is an Apple platform limitation, but it still allows verifying that the URL was opened by one of your own apps, which is useful when a URL scheme triggers privileged actions that should only be accessible from within your app suite.

{{ Info.plist }}

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package, which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TECH-0066 to locate the URL handler and check for source validation references. Run the r2 script with the `-i` option.

{{ url_scheme_handler.r2 # run.sh }}

## Observation

The output shows the `SceneDelegate`'s `scene:openURLContexts:` handler, the cross-references to `sourceApplication`, and the disassembly around each access site.

{{ output.txt }}

## Evaluation

The test case passes because `sourceApplication` is accessed from both URL handler paths: `willConnectTo` (cold launch: app is not running and iOS launches it fresh, at `0x100004c8c`) and `openURLContexts` (warm open: app is already running in the background, at `0x1000051a8`). In each disassembly block:

- `ldr x1, ... reloc.fixup.sourceApplication` loads the `sourceApplication` selector.
- `bl sym.imp.objc_msgSend` sends it to the options object, retrieving the caller's bundle ID.
- `cbz x20, ...` branches when the result is `nil` (i.e. the caller belongs to a different Apple Developer Team).

This confirms the handler reads the caller identity before processing the URL.
