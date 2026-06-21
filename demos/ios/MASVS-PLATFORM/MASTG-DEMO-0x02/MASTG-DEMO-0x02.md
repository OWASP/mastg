---
platform: ios
title: App Allowing Custom Keyboards for Sensitive Input
id: MASTG-DEMO-0x02
code: [swift]
test: MASTG-TEST-0x02
kind: fail
---

### Sample

The app presents a banking PIN field, so it handles sensitive input through the keyboard. Its app delegate implements `application(_:shouldAllowExtensionPointIdentifier:)` but returns `true` for every extension point, so it does not restrict custom keyboards app-wide. With an installed third-party keyboard enabled, the user can type the PIN with it.

{{ MastgTest.swift }}

### Steps

Let's statically analyze the app delegate's decision for the keyboard extension point.

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Execute `run.sh` to analyze the binary with @MASTG-TOOL-0073.

{{ extensionPoint.r2 # run.sh }}

### Observation

{{ output.txt }}

The script walks from the API name to the decision in three steps, which you can follow in any app:

1. **Locate the delegate method.** `f~+shouldAllowExtensionPointIdentifier` lists the matching flags. The method `method.MASTestApp.AppDelegate.application:shouldAllowExtensionPointIdentifier:` is at `0x5640`.
2. **Follow the method to its real logic.** Disassembling `0x5640` and filtering to its calls (`~bl sym`) shows it is a compiler-generated Objective-C thunk: it forwards to a Swift function (`func.00005614`, called at `0x5698`) and converts the returned Swift `Bool` to an Objective-C `BOOL` via `_convertBoolToObjCBool`. So the decision is made in `func.00005614`.
3. **Read the decision.** Disassembling `func.00005614` reveals how it computes its return value.

### Evaluation

The test case fails because the app delegate returns `true` for the keyboard extension point, so custom keyboards are allowed app-wide while the app collects a banking PIN through the keyboard.

`func.00005614` ignores its `extensionPointIdentifier` argument and returns a constant:

```asm
0x00005630      mov w8, 1
0x00005634      and w0, w8, 1
0x00005638      add sp, sp, 0x20
0x0000563c      ret
```

`w0` is the return register. It is set to `1` and masked with `1`, so the function returns `true` for every extension point. The delegate never returns `false` for the keyboard extension point, so it does not block custom keyboards.

See @MASTG-BEST-0x02 for the app-wide and field-level fixes.
