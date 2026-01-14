---
platform: ios
title: Keyboard Caching Not Prevented for Sensitive Data with r2
id: MASTG-DEMO-0076
code: [swift]
test: MASTG-TEST-0313
---

### Sample

The code snippet below creates multiple UI text inputs on the screen.

{{ MastgTest.swift }}

### Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Run `run.sh`.

{{ run.sh # textinputs.r2 }}

### Observation

The output reveals:

- 3 text input fields of type `UITextField` at addresses `0x1000055d0`, `0x10000563c`, and `0x1000056c0`.
- 1 call to `setSecureTextEntry(...)` at `0x1000046b0`.
- 2 calls to `setAutocorrectionType(...)` at `0x100004550` and `0x100004604`.
- 1 call to `setSpellCheckingType(...)` at `0x100004564`.

{{ output.asm }}

### Evaluation

The test fails because the static analysis reveals:

- a text field configured with [`setAutocorrectionType(.default)`](https://developer.apple.com/documentation/uikit/uitextinputtraits/autocorrectiontype)
- another text field with [`setAutocorrectionType(.no)`](https://developer.apple.com/documentation/uikit/uitextinputtraits/autocorrectiontype) but with spell checking enabled via [`setSpellCheckingType(.default)`](https://developer.apple.com/documentation/uikit/uitextinputtraits/spellcheckingtype)

**Interpreting the Disassembly:**

Although MastgTest.swift is written in Swift, it interacts with UIKit (an Objective-C framework). The compiler translates these interactions into calls to the `objc_msgSend` function. We analyze the arguments passed to this function using the ARM64 calling convention:

- `x0` register: holds `self` (the instance of the `UITextField`).
- `x1` register: holds the selector (the method name).
- `x2` register: holds the argument passed to that method.

**1. Name Field (FAIL):**

At address `0x100004550`, the binary loads the selector `setAutocorrectionType:`.

Immediately after, at address `0x100004558`, the instruction `mov x2, 0` sets the argument to `0`.

According to `UITextInputTraits.h`, `0` corresponds to [`UITextAutocorrectionTypeDefault`](https://developer.apple.com/documentation/uikit/uitextautocorrectiontype/default) (the following command requires @MASTG-TOOL-0070 and macOS):

```sh
grep UITextAutocorrectionType /Applications/Xcode.app/Contents/Developer/Platforms/iPhoneOS.platform/Developer/SDKs/iPhoneOS.sdk/System/Library/Frameworks/UIKit.framework/Headers/UITextInputTraits.h

typedef NS_ENUM(NSInteger, UITextAutocorrectionType) {
    UITextAutocorrectionTypeDefault, // 0
    UITextAutocorrectionTypeNo, // 1
    UITextAutocorrectionTypeYes // 2
```

Alternatively, you can view the full UITextInputTraits.h header online in public SDK mirrors on GitHub such as [GitHub - xybp888/iOS-SDKs](https://github.com/xybp888/iOS-SDKs/blob/master/iPhoneOS18.4.sdk/System/Library/Frameworks/UIKit.framework/Headers/UITextInputTraits.h#L37-L41).

This confirms that for the input field labeled `"Name"` (placeholder string constructed earlier in the function), the app explicitly allows the default behavior, enabling the keyboard cache.

Note that there is a call to `setSpellCheckingType:` at address `0x100004564`, and it sets the argument to `1` (i.e., `UITextSpellCheckingTypeNo`), which correctly disables spell checking. However, since autocorrection is still set to default, this input remains eligible for keyboard caching.

**2. Email Field (FAIL):**

For the input field labeled `"E-Mail"`, the selector `setAutocorrectionType:` is loaded at `0x100004604`. The argument is set at `0x10000460c` via `mov w2, 1`, corresponding to `UITextAutocorrectionTypeNo`.

However, no call to `setSpellCheckingType(.no)` exists for this field. Since `spellCheckingType` remains at its default value, this input is still eligible for keyboard caching and therefore fails the test.

**3. Password Field (PASS):**

The selector `setSecureTextEntry:` is loaded at address `0x1000046b0` and its argument is set to `1` at address `0x1000046b8` using `mov w2, 1`.

This confirms that [`isSecureTextEntry`](https://developer.apple.com/documentation/uikit/uitextinputtraits/issecuretextentry/) is enabled for the field labeled `"Password"`, which disables keyboard caching and correctly protects sensitive input.
