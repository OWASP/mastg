---
title: Securely Load File Content in a WebView
alias: securely-load-file-content-in-webview-ios
id: MASTG-BEST-0033
platform: ios
knowledge: [MASTG-KNOW-0076]
---

If your app relies on a static web component that loads HTML/JavaScript resources from app storage, ensure that a malicious payload cannot access other files within that storage. The app should sandbox the WebKit content using [`loadFileURL(_ URL: URL, allowingReadAccessTo readAccessURL: URL)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfileurl(_:allowingreadaccessto:)) so that the website can access only files within a specific directory.

Restricting file access prevents malicious injection payloads such as `<img src="../secret.jpg">` and `<frame src="../secret.txt">` from exfiltrating sensitive data from other directories in the filesystem.

To enforce this restriction, the app should use a dedicated directory for the static website content:

1. If the static website resides in the app bundle, set `readAccessURL` to a directory that contains only the website resources.
2. If the static website resides in app storage, create a dedicated directory for it within the `Library/Application Support` directory.

For example:

```txt
<CONTAINER>/
   Documents/
   tmp/
   Library/
      Application Support/
         sandbox-for-website/
            index.html
```
