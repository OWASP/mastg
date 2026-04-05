---
title: Migrate from UIWebView to WKWebView
alias: migrate-from-uiwebview-to-wkwebview
id: MASTG-BEST-0032
platform: ios
knowledge: [MASTG-KNOW-0076]
---

Apple deprecated [`UIWebView`](https://developer.apple.com/documentation/uikit/uiwebview) in iOS 12 in favor of [`WKWebView`](https://developer.apple.com/documentation/webkit/wkwebview) for better security and performance. Migrate your app to `WKWebView` to benefit from its improved security features, such as out-of-process rendering and enhanced JavaScript control. `WKWebView` allows you to disable JavaScript entirely, preventing script injection vulnerabilities, and provides better isolation between web content and the app, reducing the risk of memory corruption affecting the main app process. Additionally, `WKWebView` supports modern web security features like Content Security Policy (CSP), which can further enhance the security of your web content.
