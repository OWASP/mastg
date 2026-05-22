---
title: Incorrect SSL Error Handling in WebViews
platform: android
id: MASTG-TEST-0284
type: [static, code, manual]
weakness: MASWE-0052
best-practices: [MASTG-BEST-0021]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0010]
---

## Overview

This test evaluates whether an Android app has WebViews that ignore SSL/TLS certificate errors by overriding the [`onReceivedSslError(...)`](https://developer.android.com/reference/android/webkit/WebViewClient#onReceivedSslError%28android.webkit.WebView,%20android.webkit.SslErrorHandler,%20android.net.http.SslError%29) method without proper validation.

The method `onReceivedSslError(...)` is triggered when a `WebView` encounters an SSL certificate error while loading a page. By default, the `WebView` cancels the request to protect users from insecure connections. Overriding this method and calling [`SslErrorHandler.proceed()`](https://developer.android.com/reference/android/webkit/SslErrorHandler#proceed%28%29) without proper validation disables these protection.

This effectively bypasses SSL certificate checks in the `WebView`, exposing the app to [MITM attacks](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) using invalid, expired, or self-signed certificates.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where `onReceivedSslError(...)` that includes a `proceed()` is used without exception handling that properly handles SSL errors.

## Evaluation

The test case fails if `onReceivedSslError(...)` is overridden and certificate errors are ignored without proper validation or user involvement.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023, looking for cases such as:

- **Unconditionally accepting SSL errors:** calling `proceed()` without checking the nature of the error.
- **Relying only on primary error code:** using [`getPrimaryError()`](https://developer.android.com/reference/android/net/http/SslError#getPrimaryError()) for decision-making, such as proceeding if the primary error is not `SSL_UNTRUSTED`, which may overlook additional errors in the chain.
- **Suppressing exceptions silently:** catching exceptions in `onReceivedSslError(...)` without calling [`cancel()`](https://developer.android.com/reference/android/webkit/SslErrorHandler#cancel()), which allows the connection to continue silently.

According to [official Android guidance](https://developer.android.com/reference/android/webkit/WebViewClient.html#onReceivedSslError(android.webkit.WebView,%20android.webkit.SslErrorHandler,%20android.net.http.SslError)), apps should never call `proceed()` in response to SSL errors. The correct behavior is to cancel the request to protect users from potentially insecure connections. User prompts are also discouraged, as users cannot reliably evaluate SSL issues.
