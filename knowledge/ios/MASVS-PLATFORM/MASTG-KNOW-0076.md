---
masvs_category: MASVS-PLATFORM
platform: ios
title: WebViews
---

WebViews are in-app browser components for displaying interactive web content. They can be used to embed web content directly into an app's user interface. iOS WebViews support JavaScript execution by default, so script injection and Cross-Site Scripting attacks can affect them.

## Types of WebViews

There are multiple ways to include a WebView in an iOS application.

### UIWebView

[`UIWebView`](https://developer.apple.com/reference/uikit/uiwebview "UIWebView") is deprecated starting on iOS 12 and [must not be used](https://medium.com/ios-os-x-development/security-flaw-with-uiwebview-95bbd8508e3c "Security Flaw with UIWebView"). Make sure that either `WKWebView` or `SFSafariViewController` are used to embed web content. In addition to that, JavaScript cannot be disabled for `UIWebView` which is another reason to refrain from using it.

### WKWebView

[`WKWebView`](https://developer.apple.com/reference/webkit/wkwebview "WKWebView") was introduced with iOS 8 and is the appropriate choice for extending app functionality, controlling displayed content (i.e., prevent the user from navigating to arbitrary URLs) and customizing.

`WKWebView` comes with several security advantages over `UIWebView`:

- JavaScript is enabled by default but it can be completely disabled using the `javaScriptEnabled` property of `WKWebView`, preventing all script injection flaws.
- The `JavaScriptCanOpenWindowsAutomatically` can be used to prevent JavaScript from opening new windows, such as pop-ups.
- `WKWebView` implements out-of-process rendering, so memory corruption bugs won't affect the main app process.

A JavaScript Bridge can be enabled when using `WKWebView` and `UIWebView`. See Section ["Native Functionality Exposed Through WebViews"](#native-functionality-exposed-through-webviews "Native Functionality Exposed Through WebViews") below for more information.

### SFSafariViewController

[`SFSafariViewController`](https://developer.apple.com/documentation/safariservices/sfsafariviewcontroller "SFSafariViewController") is available starting on iOS 9 and should be used to provide a generalized web viewing experience. These WebViews can be easily spotted as they have a characteristic layout which includes the following elements:

- A read-only address field with a security indicator.
- An Action ("Share") button.
- A Done button, back and forward navigation buttons, and a "Safari" button to open the page directly in Safari.

<img src="Images/Chapters/0x06h/sfsafariviewcontroller.png" width="400px" />

There are a couple of things to consider:

- JavaScript cannot be disabled in `SFSafariViewController` and this is one of the reasons why the usage of `WKWebView` is recommended when the goal is extending the app's user interface.
- `SFSafariViewController` also shares cookies and other website data with Safari.
- The user's activity and interaction with a `SFSafariViewController` are not visible to the app, which cannot access AutoFill data, browsing history, or website data.
- According to the App Store Review Guidelines, `SFSafariViewController`s may not be hidden or obscured by other views or layers.

This should be sufficient for an app analysis and therefore, `SFSafariViewController`s are out of scope for the Static and Dynamic Analysis sections.

## WebView File Access

WebViews in iOS can be configured to allow access to local files using the `file://` URL scheme. The behavior and configurability differ between `UIWebView` and `WKWebView`.

### UIWebView File Access

`UIWebView` is deprecated starting on iOS 12 and should not be used. When it comes to file access:

- The `file://` scheme is always enabled.
- File access from `file://` URLs is always enabled.
- Universal access from `file://` URLs is always enabled.

These settings cannot be changed, making `UIWebView` inherently insecure for loading local content, especially if JavaScript is enabled (which cannot be disabled in `UIWebView`).

### WKWebView File Access

`WKWebView` provides more granular control over file access through undocumented properties:

- The `file://` scheme is always enabled and cannot be disabled.
- File access from `file://` URLs is disabled by default.

The following properties can be used to configure file access (both are undocumented and must be set via Key-Value Coding):

- `allowFileAccessFromFileURLs` ([`WKPreferences`](https://developer.apple.com/documentation/webkit/wkpreferences), `false` by default): enables JavaScript running in the context of a `file://` scheme URL to access content from other `file://` scheme URLs.
- `allowUniversalAccessFromFileURLs` ([`WKWebViewConfiguration`](https://developer.apple.com/documentation/webkit/wkwebviewconfiguration), `false` by default): enables JavaScript running in the context of a `file://` scheme URL to access content from any origin.

These properties can be set using `setValue:forKey:`:

Objective-C:

```objectivec
[webView.configuration.preferences setValue:@YES forKey:@"allowFileAccessFromFileURLs"];
[webView.configuration setValue:@YES forKey:@"allowUniversalAccessFromFileURLs"];
```

Swift:

```swift
webView.configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
webView.configuration.setValue(true, forKey: "allowUniversalAccessFromFileURLs")
```

### Loading Local Files

When loading local HTML files, developers typically use one of the following methods:

- [`loadHTMLString:baseURL:`](https://developer.apple.com/documentation/webkit/wkwebview/1415004-loadhtmlstring): loads HTML content from a string with a specified base URL.
- [`loadData:MIMEType:textEncodingName:baseURL:`](https://developer.apple.com/documentation/webkit/wkwebview/1415011-loaddata): loads data with a specified MIME type and base URL.
- [`loadFileURL:allowingReadAccessToURL:`](https://developer.apple.com/documentation/webkit/wkwebview/1414973-loadfileurl): loads a file from the local file system with controlled read access.

The `baseURL` parameter in the first two methods determines the effective origin of the loaded content:

- For `WKWebView`: setting `baseURL` to `nil` sets the effective origin to `"null"`, which is treated as an opaque origin and is not considered the same as other origins under the same-origin policy.
- For `UIWebView` (deprecated): setting `baseURL` to `nil` results in an effective origin with the `applewebdata://` scheme, which does not apply the same-origin policy in the same way and may allow the loaded content to access local files.

When using `loadFileURL:allowingReadAccessToURL:`, the second parameter controls what files the WebView can access:

- If it points to a single file, only that file will be accessible.
- If it points to a directory, all files in that directory will be accessible to the WebView.

Example loading a single file:

```swift
var fileURL = FileManager.default.urls(for: .libraryDirectory, in: .userDomainMask)[0]
fileURL = fileURL.appendingPathComponent("index.html")
wkWebView.loadFileURL(fileURL, allowingReadAccessTo: fileURL)
```

Example granting access to a directory:

```swift
var dirURL = FileManager.default.urls(for: .libraryDirectory, in: .userDomainMask)[0]
var fileURL = dirURL.appendingPathComponent("index.html")
wkWebView.loadFileURL(fileURL, allowingReadAccessTo: dirURL) // All files in dirURL are accessible
```

## Native Functionality Exposed Through WebViews

In iOS 7, Apple introduced APIs that allow communication between the JavaScript runtime in the WebView and the native Swift or Objective-C objects. If these APIs are used carelessly, important functionality might be exposed to attackers who manage to inject malicious scripts into the WebView (e.g., through a successful Cross-Site Scripting attack).

Both `UIWebView` and `WKWebView` provide a means of communication between the WebView and the native app. Any important data or native functionality exposed to the WebView JavaScript engine would also be accessible to rogue JavaScript running in the WebView.

**UIWebView:**

There are two fundamental ways of how native code and JavaScript can communicate:

- **JSContext**: When an Objective-C or Swift block is assigned to an identifier in a `JSContext`, JavaScriptCore automatically wraps the block in a JavaScript function.
- **JSExport protocol**: Properties, instance methods and class methods declared in a `JSExport`-inherited protocol are mapped to JavaScript objects that are available to all JavaScript code. Modifications of objects that are in the JavaScript environment are reflected in the native environment.

Note that only class members defined in the `JSExport` protocol are made accessible to JavaScript code.

**WKWebView:**

JavaScript code in a `WKWebView` can still send messages back to the native app but in contrast to `UIWebView`, it is not possible to directly reference the `JSContext` of a `WKWebView`. Instead, communication is implemented using a messaging system and using the `postMessage` function, which automatically serializes JavaScript objects into native Objective-C or Swift objects. Message handlers are configured using the method [`add(_ scriptMessageHandler:name:)`](https://developer.apple.com/documentation/webkit/wkusercontentcontroller/1537172-add "WKUserContentController add(_ scriptMessageHandler:name:)").

## WebView Network Security

The engine behind iOS WebViews is WebKit, which is also used by the Safari browser. This means that WebViews are subject to the same network security policies as Safari, including App Transport Security (ATS) and mixed content restrictions. However, developers can configure these policies differently for WebViews than for the rest of the app, which can lead to security issues if not done carefully.

### ATS

iOS WebViews are subject to the same App Transport Security (ATS) policies as the rest of the app. ATS has a specific policy for WebViews, which allows developers to relax security for web content while keeping the rest of the app secure. This is controlled by the [`NSAllowsArbitraryLoadsInWebContent`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsapptransportsecurity/nsallowsarbitraryloadsinwebcontent) key in the app's Info.plist file. If this key is set to `true`, it allows WebViews to load content over insecure HTTP connections, even if the rest of the app is restricted by ATS.

### Mixed Content

WebViews can load both HTTP and HTTPS content, which may lead to [mixed content](https://web.dev/articles/fixing-mixed-content) situations. Mixed content occurs when an HTTPS page attempts to load resources such as scripts, images, or iframes over HTTP. This weakens the security guarantees of the HTTPS page because the insecure resource could be modified by an attacker. You can learn more about mixed content in the ["Mozilla Docs for Mixed Content"](https://developer.mozilla.org/en-US/docs/Web/Security/Defenses/Mixed_content) and in the ["web.dev article for Fixing mixed content"](https://web.dev/articles/fixing-mixed-content).

Mixed content is typically divided into **active** and **passive** types. Active mixed content includes resources that can execute or modify the page, such as scripts, stylesheets, or iframes. Passive mixed content includes resources such as images, audio, or video that are displayed but do not directly execute code. In modern browsers and WebKit, **active mixed content is blocked**, while **passive mixed content is often automatically upgraded to HTTPS if possible**, or otherwise blocked and reported with a warning.

In `WKWebView`, active mixed content such as HTTP scripts loaded by an HTTPS page is generally **blocked by WebKit itself**, even if the application relaxes App Transport Security. For example, setting `NSAllowsArbitraryLoadsInWebContent` allows insecure network requests from WebViews from the ATS perspective, but it **does not disable WebKit's mixed content protections**. As a result, an HTTPS page that tries to load an HTTP script will typically have that resource blocked. See ["WebKit Features in Safari 18.0" (September 2024)](https://webkit.org/blog/15865/webkit-features-in-safari-18-0/#https).

The API [`hasOnlySecureContent`](https://developer.apple.com/documentation/webkit/wkwebview/hasonlysecurecontent) can be used after a page finishes loading to determine whether the WebView ultimately loaded only secure resources. However, it is **informational rather than preventive**. It reflects the final security state of the page, not whether the page attempted to load insecure resources that were blocked.

Because WebKit enforces these protections and they cannot be disabled through public `WKWebView` APIs, the recommended approach is still to ensure that all content loaded into WebViews is served over HTTPS and to avoid unnecessarily relaxing ATS policies such as `NSAllowsArbitraryLoadsInWebContent`. The only way for a page to load mixed content is for the page itself to be loaded as HTTP.

## Loading Content

### Remote URLs into WebViews

iOS apps can load remote URLs into a WebView using the `load(_:)` method with a `URLRequest` containing the target URL. If the URL is derived from attacker-controlled input without proper validation, it can lead to security issues such as loading malicious content or phishing pages. For example, if an app allows users to input a URL that is then loaded into a WebView without validation, an attacker could input a URL that points to a malicious site, leading to potential data theft or other attacks.

### Local Files into WebViews

iOS WebViews can load local files using the `loadFileURL(_:allowingReadAccessTo:)` method. This method allows developers to specify a local file URL to load and a read access URL that defines the scope of local files the WebView can access. If the read access URL is set too broadly, it can allow malicious content loaded into the WebView to access sensitive files on the device. For example, if the read access URL is set to the entire `Documents` directory, a malicious page could potentially read any file in that directory, which may include sensitive user data.

Other APIs that can be used to load content into WebViews include `loadHTMLString(_:baseURL:)`, which allows loading HTML content directly as a string, and `loadData(_:MIMEType:characterEncodingName:baseURL:)`, which allows loading raw data with a specified MIME type and character encoding.

The `baseURL` parameter in these methods can also be a source of security issues if it is derived from attacker-controlled input, as it can affect the origin of the loaded content and potentially lead to cross-origin issues or allow access to unintended resources.

## File Access in WebViews

On iOS, file access is primarily constrained by the `allowingReadAccessTo` directory boundary combined with the app sandbox. In contrast, on Android, file access behavior is primarily controlled by WebView settings that modify how `file://` origins interact with other files and network resources, and there is no direct per load directory restriction equivalent to `allowingReadAccessTo`.

### Local File Access Scope

On iOS, `WKWebView.loadFileURL(_:allowingReadAccessTo:)` loads a local file while granting the WebView permission to read additional files inside a specific directory. The `allowingReadAccessTo` parameter defines the maximum directory scope that WebKit may access for that load. Any resources requested by the page must reside within that directory or its subdirectories.

This effectively creates a native file read boundary. If the scope is set broadly, such as the entire `Documents` directory or the app container root, content executing in the WebView may be able to trigger reads of other files within that permitted area.

The access remains limited by the iOS app sandbox. Even if the read scope points to the container root, the WebView cannot access system files or data belonging to other applications.

In contrast to Android, iOS provides this explicit per load directory boundary. Android WebView does not have a direct equivalent to `allowingReadAccessTo`. Instead, file access behavior is controlled through global WebView settings and the underlying application sandbox.

### File URL Security Flags

Two settings are frequently discussed in relation to WebView file access.

`allowFileAccessFromFileURLs`
`allowUniversalAccessFromFileURLs`

On iOS these are internal WebKit preferences and are not public WKWebView APIs. Attempting to set them through key value coding is unsupported and may stop working across system versions.

Conceptually they influence how the web security model treats `file://` origins. The first allows JavaScript in one local file to access other local files. The second allows `file://` pages to perform cross origin network requests.

Even if these preferences are enabled through unsupported mechanisms, they do not bypass the native file boundary defined by `allowingReadAccessTo`.

In contrast to iOS, Android exposes these settings as public APIs through `WebSettings.setAllowFileAccessFromFileURLs` and `WebSettings.setAllowUniversalAccessFromFileURLs`. Misconfiguration of these flags has been repeatedly observed in Android WebView vulnerability reports because they weaken origin isolation for local files.

### Conditions and Realistic Exfiltration Paths

Broad file read access alone does not expose data. Exploitation generally requires two elements. The WebView is granted access to sensitive directories through `loadFileURL(_:allowingReadAccessTo:)`, and the attacker can execute JavaScript in that WebView. Script execution can occur through injected HTML, attacker controlled pages loaded by the app, deep links that open untrusted URLs in a WebView, or exposed JavaScript bridges.

If these conditions are met, malicious script may trigger the WebView to load additional files within the permitted directory. Access remains limited to the application sandbox, typically files in `Documents`, caches, or other app storage.

Once data is accessible, exfiltration usually relies on normal WebView capabilities. The most common mechanism is sending the data to an attacker controlled server using standard web APIs such as `fetch` or `XMLHttpRequest`. If the application exposes native functionality through interfaces such as `WKScriptMessageHandler`, attacker controlled JavaScript can also pass data to the host application, which may then transmit it through the app's networking functionality. In some cases the script may store data in application controlled storage or APIs that are later uploaded or synchronized by the app.

In contrast to Android, iOS enforces a per load directory boundary through `allowingReadAccessTo`. Android WebView does not provide an equivalent mechanism and instead relies on global settings such as `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs` to control how `file://` content interacts with other files and network resources.
