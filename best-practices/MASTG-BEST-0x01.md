---
title: Securely Load File Content in a WebView
alias: securely-load-file-content-in-webview-ios
id: MASTG-BEST-0x01
platform: ios
knowledge: [MASTG-KNOW-0076]
---

## Recommendation

When loading local content in iOS WebViews, follow these practices to prevent unauthorized file access and data exfiltration:

### Use WKWebView Instead of UIWebView

`UIWebView` is deprecated since iOS 12 and should not be used. Always use `WKWebView` for displaying web content, as it provides better security controls and performance.

### Avoid Enabling File Access from File URLs

For `WKWebView`, the properties `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs` are set to `false` by default and should remain disabled unless there is a specific, well-justified need:

- `allowFileAccessFromFileURLs` (`WKPreferences`): enables JavaScript running in the context of a `file://` scheme URL to access content from other `file://` scheme URLs.
- `allowUniversalAccessFromFileURLs` (`WKWebViewConfiguration`): enables JavaScript running in the context of a `file://` scheme URL to access content from any origin.

These properties are **undocumented** and can only be set using Key-Value Coding (KVC). Avoid setting them to `true`:

```swift
// DO NOT DO THIS unless absolutely necessary
webView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
webView.configuration.setValue(true, forKey: "allowUniversalAccessFromFileURLs")
```

If you must enable these properties, ensure that:

- The WebView only loads trusted content from controlled sources.
- Proper input validation and sanitization are in place.
- The app does not store sensitive data in locations accessible to the WebView.

### Load Local Files Securely

When loading local HTML files using `loadHTMLString:baseURL:` or `loadData:MIMEType:textEncodingName:baseURL:`, set the `baseURL` parameter appropriately:

- For `WKWebView`: setting `baseURL` to `nil` sets the effective origin to "null", which is safe and prevents cross-origin access.
- Alternatively, use the app's resource URL: `[NSBundle mainBundle].resourceURL`.

Example in Swift:

```swift
let htmlPath = Bundle.main.url(forResource: "index", withExtension: "html")!
let htmlString = try! String(contentsOf: htmlPath, encoding: .utf8)
webView.loadHTMLString(htmlString, baseURL: Bundle.main.resourceURL)
```

### Use loadFileURL Carefully

When using `loadFileURL:allowingReadAccessToURL:`, ensure that the `allowingReadAccessToURL` parameter points to a single file rather than a directory:

```swift
// Good: Restricting access to a single file
let fileURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    .appendingPathComponent("safe.html")
webView.loadFileURL(fileURL, allowingReadAccessTo: fileURL)

// Bad: Granting access to an entire directory
let dirURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
webView.loadFileURL(fileURL, allowingReadAccessTo: dirURL) // Avoid this
```

If you must grant access to a directory, ensure it does not contain any sensitive data.

## Rationale

File access from `file://` URLs in WebViews can be exploited by malicious content to:

- Access sensitive data stored in the app's sandbox.
- Exfiltrate data to remote servers.
- Bypass same-origin policy protections.

By keeping file access disabled and using secure loading methods, you minimize the attack surface and protect user data.

## Caveats

- These settings only affect `WKWebView`. `UIWebView` always allows file access and cannot be secured, which is one reason it was deprecated.
- Disabling JavaScript (`javaScriptEnabled = false`) can also mitigate some risks, but this may impact functionality if the WebView needs to execute scripts.
- Even with these protections, ensure that WebViews only load content from trusted sources to prevent XSS and other web-based attacks.

## References

- [WKWebView Apple Documentation](https://developer.apple.com/documentation/webkit/wkwebview)
- [WKPreferences Apple Documentation](https://developer.apple.com/documentation/webkit/wkpreferences)
- [loadFileURL:allowingReadAccessToURL: Apple Documentation](https://developer.apple.com/documentation/webkit/wkwebview/1414973-loadfileurl)
- [UIWebView Deprecation](https://developer.apple.com/documentation/uikit/uiwebview)
- [WebKit Source - allowFileAccessFromFileURLs](https://github.com/WebKit/webkit/blob/master/Source/WebKit/UIProcess/API/Cocoa/WKPreferences.mm#L470)
