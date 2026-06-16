---
platform: ios
title: DOM Inspection Using evaluateJavaScript Without Content World Isolation
code: [swift]
id: MASTG-DEMO-0145
test: MASTG-TEST-0379
kind: fail
---

## Sample

This sample demonstrates a `WKWebView` that reads sensitive content from the DOM (a recipient account number) using `evaluateJavaScript(_:completionHandler:)` without specifying a content world. The script runs in the `.page` world, where the prototype chain is shared with page JavaScript. A malicious page can override `document.querySelector` before the call runs:

```javascript
// Attacker controlled page script
document.querySelector = function(selector) {
    return { textContent: "ATTACKER_CONTROLLED" };
};
```

After this override, the `evaluateJavaScript` call returns the attacker value instead of the real DOM content. The native code receives and acts on poisoned data.

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the app. The main binary is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ evaluate_js_no_world.r2 }}

{{ run.sh }}

## Observation

The script finds two xrefs to `evaluateJavaScript:completionHandler:`, both inside `MastgTest.webView(_:didFinish:)`. The disassembly at each call site is shown below.

{{ output.txt }}

## Evaluation

The test case fails because the app calls `evaluateJavaScript:completionHandler:` to read security-sensitive DOM content in the page world.

The output shows two call sites. The first (`0x100004804`) is the attacker-injected prototype override that is part of this demo's setup and not the app's production logic. At `0x1000047e0`, `add x8, x8, 0x3f0` resolves to the string `"document.querySelector = function(selector) { return { textContent: \"ATTACKER_CONTROLLED\" }; };"` in the read-only data section. This string is bridged to an `NSString` (stored in `x21`), then passed as `x2` to `objc_msgSend` at `0x100004814` with `x3 = 0` (no completion handler), confirming this is the page-level override.

The second call site (`0x1000048b0`) is the security-relevant one. At `0x100004824`, `add x8, x8, 0x4a0` resolves to `"document.querySelector('#recipient_account_number').textContent"`. After Swift string boxing (`sub`, `orr`) and bridging to `NSString` via `Foundationbool_...ridgeToObjectiveCSo8NSStringCyF_`, the result is stored in `x21` at `0x10000483c`. At `0x100004840` to `0x1000048a0`, a completion block is built and heap-copied via `_Block_copy`; the result lands in `x20`. Then at `0x1000048b0`, `ldr x1, [x23, 0x4a8]` loads the selector `evaluateJavaScript:completionHandler:` into `x1`, and:

- `x0` ← `x19` (the `WKWebView` instance)
- `x2` ← `x21` (`"document.querySelector('#recipient_account_number').textContent"`)
- `x3` ← `x20` (the completion block)

The `objc_msgSend` at `0x1000048c0` therefore calls `[webView evaluateJavaScript:@"document.querySelector('#recipient_account_number').textContent" completionHandler:block]`. Because the selector is `evaluateJavaScript:completionHandler:` rather than the content-world variant `evaluateJavaScript:inFrame:inContentWorld:completionHandler:`, the script runs in the `.page` world. Since the first call site already ran in the same world and overrode `document.querySelector`, this call receives the attacker-controlled value instead of the real DOM content.
