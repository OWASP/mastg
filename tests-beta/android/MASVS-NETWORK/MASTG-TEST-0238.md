---
title: Runtime Use of Android Network APIs Transmitting Cleartext Traffic
platform: android
id: MASTG-TEST-0238
type: [dynamic]
weakness: MASWE-0050
profiles: [L1, L2]
---

## Overview

This test intercepts Android network API (@MASTG-KNOW-0109) in the packages `java.net`, `android.net` and `android.webkit` used to establish network connections in order to detect insecure URI schemes.

This is beneficial if a security tester is not able to intercept the network traffic as described in @MASTG-TEST-0236. Further, it is possible to detect insecure URI schemes in obfuscated apps as solely @MASTG-TECH-0048 may not be sufficient to detect them.

## Steps

Hook methods which take URI schemes as arguments and verify, that they are not insecure meaning they refer to a protocol which does not support transport layer encryption for example.

The following list enumerates @MASTG-KNOW-0109 which should be tested:

- `java.net.URL(String spec)`
- `java.net.URL(String protocol, String host, int port, String file)`
- `java.net.URL(String protocol, String host, int port, String file, URLStreamHandler handler)`
- `java.net.URL(String protocol, String host, String file)`
- `java.net.URI(String spec)`
- `java.net.URI(String scheme, String ssp, String fragment)`
- `java.net.URI(String scheme, String host, String path, String fragment)`
- `java.net.URI(String scheme, String authority, String path, String query, String fragment)`
- `android.net.Uri.parse(String uriString)`
- `android.net.Uri.Builder.scheme(String scheme)`
- `android.webkit.WebView.loadUrl(String url)`
- `android.webkit.WebView.loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl)`
- `android.webkit.WebView.postUrl(String url, byte[] postData)`
- `android.webkit.WebViewClient.doUpdateVisitedHistory(WebView view, String url, boolean isReload)`
- `android.webkit.WebViewClient.onLoadResource(WebView view, String url)`
- `android.webkit.WebViewClient.onPageCommitVisible(WebView view, String url)`
- `android.webkit.WebViewClient.onPageFinished(WebView view, String url)`
- `android.webkit.WebViewClient.onPageStarted(WebView view, String url, Bitmap favicon)`
- `android.webkit.WebViewClient.shouldInterceptRequest(WebView view, String url)`
- `android.webkit.WebViewClient.shouldOverrideUrlLoading(WebView view, String url)`
- `android.media.MediaPlayer.setDataSource(String path)`

Aside from these methods, custom code, or third-party libraries may also contain places where clear text transmission is initiated.

## Observation

The output contains information about the used URI schemes.

## Evaluation

The test passes, if _all_ URI schemes are considered secure and fails, if _any_ URI is considered insecure.

Examples of well known protocols and their secure and insecure URI are:

| secure       | insecure |
|--------------|----------|
| https        | http     |
| wss          | ws       |
| sips         | sip      |
| ldaps        | ldap     |
| ftps / sftp  | ftp      |
| rtsps        | rtsp     |
| stuns        | stun     |

A comprehensive list of well known URI schemes can found here: [iana Uniform Resource Identifier (URI) Schemes
](https://www.iana.org/assignments/uri-schemes/uri-schemes.xhtml)
