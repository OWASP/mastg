---
title: Validate WebView Input
alias: validate-webview-input
id: MASTG-BEST-0034
platform: ios
knowledge: [MASTG-KNOW-0076]
---

Always treat any data passed to a [`WKWebView`](https://developer.apple.com/documentation/webkit/wkwebview) as untrusted unless it is fully controlled by the app. This includes URLs loaded through [`load(_:)`](https://developer.apple.com/documentation/webkit/wkwebview/load(_:)), local files loaded through [`loadFileURL`](https://developer.apple.com/documentation/webkit/wkwebview/loadfileurl(_:allowingreadaccessto:)), HTML passed to [`loadHTMLString`](https://developer.apple.com/documentation/webkit/wkwebview/loadhtmlstring(_:baseurl:)), JavaScript passed to [`evaluateJavaScript`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)), and any data inserted into the rendered page.

If the app loads a URL into a `WKWebView`, the URL should be parsed and validated against a strict allowlist of expected schemes, hosts, paths, and other relevant components. Do not allow attacker-controlled input, such as deep links, custom URL schemes, pasted text, or server-supplied values, to determine arbitrary WebView destinations.

For deep links and custom URL schemes, only accept known commands and fixed destinations. Do not forward arbitrary URL parameters directly into `WKWebView.load(_:)`.

If untrusted data must be displayed inside web content, avoid HTML and JavaScript injection patterns such as [`innerHTML`](https://developer.mozilla.org/en-US/docs/Web/API/Element/innerHTML) or string built `evaluateJavaScript` calls. Prefer safe text only rendering and context appropriate escaping.

If local content is loaded with `loadFileURL`, keep `allowingReadAccessTo` as narrow as possible and never combine broad local file access with untrusted HTML or script injection.
