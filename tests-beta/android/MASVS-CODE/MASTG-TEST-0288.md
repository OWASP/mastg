---
title: Unvalidated URL from Deep Link Loaded in WebView
platform: android
id: MASTG-TEST-0288
type: [static]
weakness: MASWE-0088
profiles: [L1, L2]
---

### Overview

This vulnerability occurs when an application receives a URL from an external source, such as a deep link's query parameter, and loads it into a WebView without proper validation. A malicious application could send a specially crafted Intent containing a deep link with a malicious URL. When the vulnerable app's WebView loads this URL, the embedded script executes within the context of the app, leading to a Cross-Site Scripting (XSS) vulnerability. This can be used to steal session cookies, inject fake content, or perform actions on behalf of the user.

### Steps

Run a static ancalysis tool such as @MASTG-TOOL-0110 on the codebase to detect data flows from deep link parameters (e.g., `getQueryParameter()`) to dangerous sinks (e.g., `WebView.loadUrl()`).

### Observation

The output file shows a data flow where data from an Intent is used in `WebView.loadUrl()` without prior sanitization or validation.

### Evaluation

The test fails due to the application loading an unvalidated URL from an untrusted Intent extra into a WebView. A malicious application can create an Intent with a deep link containing a URL pointing to a malicious website. When this URL is loaded by the vulnerable WebView, the user is redirected to the attacker's site.
