---
platform: ios
title: Attacker-Controlled URI in WebViews
id: MASTG-TEST-0332
type: [static]
weakness: MASWE-0071
best-practices: [MASTG-BEST-0034]
profiles: [L1, L2, P]
knowledge: [MASTG-KNOW-0076]
---

## Overview

iOS apps can dynamically load content into a [`WKWebView`](https://developer.apple.com/documentation/webkit/wkwebview) using various URL load methods. These methods can render both remote web content and locally stored files.

The following WKWebView APIs are commonly targeted if they process untrusted input:

**Remote URL Loading:**

- [`load(_:)`](https://developer.apple.com/documentation/webkit/wkwebview/load(_:))
- [`load(_:mimeType:characterEncodingName:baseURL:)`](https://developer.apple.com/documentation/webkit/wkwebview/load(_:mimetype:characterencodingname:baseurl:))

**Local URL and Content Loading:**

- [`loadFileRequest(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfilerequest(_:allowingreadaccessto:))
- [`loadFileURL(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfileurl(_:allowingreadaccessto:))
- [`loadHTMLString(_:baseURL:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadhtmlstring(_:baseurl:))

Regardless of the source, passing a URL originating from attacker-controlled input (for example through a deep link, custom URL scheme, or user-supplied data from the UI) directly to the `WKWebView` URL load methods can lead to vulnerabilities such as unauthorized redirection, Cross-Site Scripting (XSS), or local file disclosure.

This test checks whether the app passes attacker-controlled input to `WKWebView` URL load APIs without adequate URL validation.

## Steps

1. Extract the app as described in @MASTG-TECH-0058.
2. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary, looking for calls to any `WKWebView` URL load APIs.

## Observation

The output should contain a list of locations in the binary where `WKWebView` URL load APIs are called.

## Evaluation

The test case fails if any call to `WKWebView` URL load APIs is found where the URL is derived from attacker-controlled input without proper validation.

Inspect each reported code location using @MASTG-TECH-0076.

- Trace where the URL originates.
- Determine whether it is derived from attacker-controlled input, for example a custom URL scheme parameter, a deep link component, or unsanitized user input from the UI.
- Verify that the URL is adequately validated before being passed to `WKWebView` URL load APIs.
