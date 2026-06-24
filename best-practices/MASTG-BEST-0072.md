---
title: Validate Input Parameters in Universal Link Handlers
alias: validate-input-parameters-in-universal-link-handlers
id: MASTG-BEST-0072
platform: ios
knowledge: [MASTG-KNOW-0080]
---

Validate and sanitize the path and query parameters of every incoming universal link before using them in security-sensitive operations. Universal link verification only proves that the request targets a domain your app is associated with (@MASTG-KNOW-0080); it does not validate the rest of the URL. Anyone can craft a link to your verified domain with arbitrary path and query values and get the user to open it, so treat the `webpageURL` and its parameters as untrusted input.

Apple makes this explicit in ["Supporting universal links in your app"](https://developer.apple.com/documentation/xcode/supporting-universal-links-in-your-app "Supporting universal links in your app"): universal links are an entry point into your app, so validate all URL parameters, discard malformed URLs, and limit the actions a link can trigger to those that do not put the user's data at risk.

## Confirm the Activity and Read the Verified URL

Handle the activity only when its type is `NSUserActivityTypeBrowsingWeb` and read the URL from [`webpageURL`](https://developer.apple.com/documentation/foundation/nsuseractivity/1418086-webpageurl), then parse it with [`URLComponents`](https://developer.apple.com/documentation/foundation/urlcomponents):

```swift
guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
      let url = userActivity.webpageURL,
      let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
    return false
}
```

## Convert to Expected Types

When a parameter represents a numeric value, convert it explicitly with [`Int.init`](https://developer.apple.com/documentation/swift/int/init(_:)-7l2mf) or [`Double.init`](https://developer.apple.com/documentation/swift/double/init(_:)-90wjl) and handle failure gracefully. Never use the raw string from [`URLQueryItem.value`](https://developer.apple.com/documentation/foundation/urlqueryitem/value) directly in operations that expect a specific type.

```swift
guard let amountString = components.queryItems?.first(where: { $0.name == "amount" })?.value,
      let amount = Int(amountString) else {
    return false
}
```

## Check Bounds and Ranges

After type conversion, verify the value falls within acceptable bounds before acting on it. For financial or resource-sensitive operations, enforce both minimum and maximum limits.

```swift
guard amount > 0, amount <= 10000 else {
    return false
}
```

## Sanitize Path and String Parameters

Without sanitization, a crafted universal link can target different parts of the app:

- **Path traversal**: a value like `path=../../private/secrets.txt` can escape intended directories if used in file operations. Resolve paths with [`URL.standardized`](https://developer.apple.com/documentation/foundation/url/standardized) and verify the result stays within the expected base directory. See @MASTG-BEST-0033 for secure file loading in WebViews.
- **Script injection**: a value like `q=<script>alert(1)</script>` can execute arbitrary JavaScript if rendered in a [`WKWebView`](https://developer.apple.com/documentation/webkit/wkwebview). See @MASTG-BEST-0034 for WebView input validation guidance.
- **Command or query injection**: values interpolated into shell commands, SQL queries, or predicate strings can alter their logic. Use parameterized queries and avoid string interpolation for constructing commands.

Use allowlists for the path or for parameters that select a resource or action when the set of valid inputs is known. Reject any value that does not match rather than attempting to strip or escape individual characters.

The same validation applies regardless of how the link is delivered, that is, through [`application(_:continue:restorationHandler:)`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623072-application), [`scene(_:continue:)`](https://developer.apple.com/documentation/uikit/uiscenedelegate/3238056-scene), or SwiftUI's [`onContinueUserActivity(_:perform:)`](https://developer.apple.com/documentation/swiftui/view/oncontinueuseractivity(_:perform:)).
