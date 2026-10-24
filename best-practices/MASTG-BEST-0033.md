---
title: Securely Load File Content in a WebView
alias: securely-load-file-content-in-webview-ios
id: MASTG-BEST-0033
platform: ios
knowledge: [MASTG-KNOW-0076]
---

## Avoid Enabling `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs`

For `WKWebView`, `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs` are not part of the public iOS `WKWebView` API. They are commonly accessed through Key-Value Coding (KVC), but should remain disabled unless there is a specific, well justified need.

If you must enable these properties, ensure that:

- The WebView only loads trusted content from controlled sources.
- Proper input validation and sanitization are implemented.
- The app does not store sensitive data in locations accessible to the WebView.

These settings apply only to `WKWebView`. `UIWebView` historically allowed broader local file access and lacked the modern isolation and control model provided by `WKWebView`, which is one reason `UIWebView` was deprecated and replaced. See @MASTG-BEST-0032.

## Load Local Files Securely

When loading local HTML using [`loadHTMLString(_:baseURL:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadhtmlstring(_:baseurl:)/) or [`load(_:mimeType:characterEncodingName:baseURL:)`](https://developer.apple.com/documentation/webkit/wkwebview/load(_:mimetype:characterencodingname:baseurl:)), set the `baseURL` deliberately.

- For `WKWebView`, setting `baseURL` to `nil` gives the document an opaque origin. This prevents it from being treated as same origin with local files and helps stop access to other local resources.
- If the page needs bundled subresources such as CSS, images, or JavaScript, prefer [`loadFileURL(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfileurl(_:allowingreadaccessto:)) or [`loadFileRequest(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfilerequest(_:allowingreadaccessto:)) with a narrowly scoped read access URL.
- If you do use a `file://` base URL, keep it limited to a controlled resource location such as the app bundle.

Avoid broad `file://` base URLs unless they are strictly necessary.

## Use `loadFileURL` and `loadFileRequest` Carefully

When using [`loadFileURL(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfileurl(_:allowingreadaccessto:)) or [`loadFileRequest(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfilerequest(_:allowingreadaccessto:)), ensure that the `allowingReadAccessTo` parameter grants the **minimum required file system scope**.

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

If directory access is required, ensure that the directory contains only WebView assets and no sensitive app data.

## Additional Considerations

Even when these precautions are followed, WebViews should only load content from trusted sources. If attacker-controlled JavaScript executes in a WebView that can read local files, it may read and exfiltrate sensitive data from the app sandbox.

- Disable content JavaScript when the WebView only displays static content, using [`WKWebpagePreferences.allowsContentJavaScript = false`](https://developer.apple.com/documentation/webkit/wkwebpagepreferences/allowscontentjavascript).
- Avoid loading untrusted input into WebViews to prevent HTML or JavaScript injection.
- Keep WebView accessible files separate from app data, secrets, and credentials.
- Prefer loading content from the app bundle or controlled sources instead of broad `file://` paths.
- Consider [App Bound Domains](https://webkit.org/blog/10882/app-bound-domains/) for WebViews that should only access app controlled domains and use powerful WebKit APIs.
