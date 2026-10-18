---
title: Use WKContentWorld Isolation for DOM Inspection Scripts
alias: use-wkcontentworld-isolation-for-dom-inspection-scripts
id: MASTG-BEST-0061
platform: ios
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

When an app uses [`evaluateJavaScript(_:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) or [`WKUserScript`](https://developer.apple.com/documentation/webkit/wkuserscript) to read data from the DOM (for example, to extract form field values, account details, or page metadata), that code runs in the `.page` world by default. In the `.page` world, all built-in prototypes are shared with page JavaScript. A malicious script running on the page can override `document.querySelector`, `Element.prototype.getAttribute`, or any other native function before your inspection code runs, causing it to receive manipulated results.

Use the content-world variants of these APIs (introduced in iOS 14, see @MASTG-KNOW-0139) to run DOM inspection code in an isolated world where the prototype chain cannot be tampered with by page JavaScript.

## Use the Content World Variant of evaluateJavaScript

Prefer [`evaluateJavaScript(_:in:in:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:in:in:completionhandler:)), which accepts a [`WKContentWorld`](https://developer.apple.com/documentation/webkit/wkcontentworld):

```swift
let appWorld = WKContentWorld.world(withName: "AppWorld")

// Vulnerable: runs in the page world, prototype chain is shared with page JS
webView.evaluateJavaScript("document.getElementById('balance').textContent") { value, error in
    process(value)
}

// Safe: runs in an isolated world, prototype chain is independent
webView.evaluateJavaScript("document.getElementById('balance').textContent",
                            in: nil,
                            in: appWorld) { result in
    if case .success(let value) = result { process(value) }
}
```

In the isolated world, `document.getElementById` cannot be overridden by page JavaScript. Even if the page runs `document.getElementById = () => fakeElement` before your script executes, your isolated world's binding is unaffected.

## Validate DOM Data Before Acting on It

Content world isolation protects the inspection code itself from being hijacked, but it does not prevent the page from placing misleading content in the DOM. If an attacker controls the page, element text content or input values may be manipulated before your script reads them. Treat all values read from the DOM as untrusted input and validate them on the native side before acting on them in a security-relevant way.
