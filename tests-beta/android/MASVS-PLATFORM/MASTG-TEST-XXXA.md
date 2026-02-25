---
title: Unvalidated URL from Deep Link Loaded in WebView
platform: android
id: MASTG-TEST-XXXA
type: [static]
weakness: MASWE-0071
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0019]
---

## Overview

This vulnerability arises when an app accepts a URL from an external source such as a deep link query parameter and loads it into a WebView without validation. An attacker can craft a malicious Intent containing a deep link with a harmful URL. When loaded, the WebView executes the embedded script in the app's context, resulting in a Cross-Site Scripting (XSS) vulnerability. This could allow theft of session cookies, injection of fake content, or unauthorized actions on behalf of the user.

## Steps

1. Run @MASTG-TECH-XXXX on the app to look for data flows from deep link parameters (e.g., `getQueryParameter()`) to dangerous sinks (e.g., `WebView.loadUrl()`).

## Observation

The output should contain a data flow where data from an Intent is used in `WebView.loadUrl()` without prior sanitization or validation.

## Evaluation

The test case fails if the application loads an unvalidated URL from an untrusted Intent extra into a WebView. A malicious application can create an Intent with a deep link containing a URL pointing to a malicious website. When this URL is loaded by the vulnerable WebView, the user is redirected to the attacker's site.
