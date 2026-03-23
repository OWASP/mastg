---
title: Securely Load File Content in a WebView
alias: securely-load-file-content-in-webview-ios
id: MASTG-BEST-0033
platform: ios
knowledge: [MASTG-KNOW-0076]
---

## Avoid Enabling `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs`

For `WKWebView`, the properties `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs` are disabled by default and should remain disabled unless there is a specific, well-justified need. These properties are not part of the public iOS `WKWebView` API and are typically accessed through Key-Value Coding (KVC).

If you must enable these properties, ensure that:

- The WebView only loads trusted content from controlled sources.
- Proper input validation and sanitization are implemented.
- The app does not store sensitive data in locations accessible to the WebView.

These settings apply only to `WKWebView`. `UIWebView` always allowed unrestricted file access and lacked modern security controls, which is one reason it was deprecated and replaced by `WKWebView`.

## Load Local Files Securely

When loading local HTML files using `loadHTMLString(_:baseURL:)` or `load(_:mimeType:characterEncodingName:baseURL:)`, set the `baseURL` parameter appropriately:

- For `WKWebView`, setting `baseURL` to `nil` results in a `null` origin, which prevents the page from accessing other local resources.
- Alternatively, use a controlled resource location such as the app bundle (`Bundle.main.resourceURL`).

Avoid using broad `file://` base URLs unless strictly necessary.

## Use `loadFileURL` Carefully

When using `loadFileURL(_:allowingReadAccessTo:)`, ensure that the `allowingReadAccessTo` parameter grants the **minimum required file system scope**.

```swift
// Good: Restrict access to a specific file
let fileURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    .appendingPathComponent("safe.html")

webView.loadFileURL(fileURL, allowingReadAccessTo: fileURL)
```

```swift
// Risky: Grants access to an entire directory
let dirURL = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]

webView.loadFileURL(fileURL, allowingReadAccessTo: dirURL) // Avoid if possible
```

If directory access is required, ensure that the directory contains only WebView assets and no sensitive application data.

## Additional Considerations

Even when these precautions are followed, WebViews should only load content from trusted sources. If attacker-controlled JavaScript executes in a WebView that has access to local files, it may read and exfiltrate sensitive data from the application sandbox.

- Consider disabling JavaScript (`javaScriptEnabled = false`) if the WebView only displays static content.
- Avoid loading untrusted input into WebViews to prevent HTML or JavaScript injection.
- Keep WebView-accessible files separate from application data or credentials.
- Prefer loading content from the application bundle or controlled sources instead of broad `file://` paths.
