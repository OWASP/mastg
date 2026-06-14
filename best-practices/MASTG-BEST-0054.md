---
title: Validate Input Parameters in Custom URL Scheme Handlers
alias: validate-input-parameters-in-custom-url-scheme-handlers
id: MASTG-BEST-0054
platform: ios
knowledge: [MASTG-KNOW-0079]
---

Validate and sanitize all URL parameters before using them in your custom URL scheme handler. Since any app on the device can open a custom URL scheme, treat all incoming parameter values as untrusted input.

## Convert to Expected Types

When a parameter represents a numeric value, convert it explicitly using [`Int.init`](https://developer.apple.com/documentation/swift/int/init(_:)-7l2mf) or [`Double.init`](https://developer.apple.com/documentation/swift/double/init(_:)-90wjl) and handle conversion failure gracefully. Never use the raw string from [`URLQueryItem.value`](https://developer.apple.com/documentation/foundation/urlqueryitem/value) directly in operations that expect a specific type.

```swift
guard let amountString = components?.queryItems?.first(where: { $0.name == "amount" })?.value,
      let amount = Int(amountString) else {
    return
}
```

## Check Bounds and Ranges

After type conversion, verify that the value falls within acceptable bounds before acting on it. For financial or resource-sensitive operations, enforce both minimum and maximum limits.

```swift
guard amount > 0, amount <= 10000 else {
    return
}
```

## Sanitize String Parameters

Without sanitization, an attacker can craft URLs with malicious parameter values targeting different parts of the app:

- **Path traversal**: a parameter like `path=../../private/secrets.txt` can escape intended directories if used in file operations. Resolve paths with [`URL.standardized`](https://developer.apple.com/documentation/foundation/url/standardized) and verify the result stays within the expected base directory. See @MASTG-BEST-0033 for secure file loading in WebViews.
- **Script injection**: a parameter like `q=<script>alert(1)</script>` can execute arbitrary JavaScript if rendered in a [`WKWebView`](https://developer.apple.com/documentation/webkit/wkwebview). See @MASTG-BEST-0034 for WebView input validation guidance.
- **Command or query injection**: parameter values interpolated into shell commands, SQL queries, or predicate strings can alter their logic. Use parameterized queries and avoid string interpolation for constructing commands.

Use allowlists for expected values when the set of valid inputs is known. Reject any value that does not match rather than attempting to strip or escape individual characters.
