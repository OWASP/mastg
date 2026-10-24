---
title: Prefer Origin Scoped Messaging Over Legacy JavaScript Bridges
alias: prefer-origin-scoped-messaging-over-legacy-javascript-bridges
id: MASTG-BEST-0035
platform: android
knowledge: [MASTG-KNOW-0018]
---

JavaScript bridges are not inherently unsafe, but they are a high-impact `WebView` feature and should only be exposed to content you fully trust. The main risk is not the bridge alone, but the combination of a bridge with untrusted or weakly validated content.

## Avoid the Legacy `addJavascriptInterface` Model

The [legacy `addJavascriptInterface`](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge#addjavascriptinterface) mechanism is exposed to every frame in the `WebView`, including iframes, and does not provide origin-based access control. This makes it unsuitable as a security boundary when the `WebView` may render untrusted or weakly validated content.

Android also [notes that one safer way to use `addJavascriptInterface()`](https://developer.android.com/privacy-and-security/risks/insecure-webview-native-bridges#addjavascriptinterface-risks-target-api-level-21-or-higher) is to [target API level 21 or higher](https://developer.android.com/reference/android/webkit/WebView#addJavascriptInterface(java.lang.Object,%20java.lang.String)), because then JavaScript can only access methods explicitly annotated with `@JavascriptInterface`, whereas older target levels also exposed public fields of the injected object. Even with that improvement, the mechanism still lacks origin-based access control, so Android recommends newer origin-aware alternatives for modern bridge designs.

## Prefer `addWebMessageListener` When a Bridge Is Required

Android explicitly documents [`addWebMessageListener`](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge) as the **recommended** modern bridge mechanism. It is described as the most modern and recommended approach, and the [mechanism comparison table](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge#summary-mechanisms) marks it as **Recommended: Yes** and **Security: Highest (Allowlist-based)**.

Use it when you need communication between web content and native code and can define a narrow allowlist of trusted origins. Configure a strict `allowedOriginRules` set, register the listener before calling `loadUrl()`, and validate the sender information in the callback before processing messages.

## Use `postWebMessage` Only as an Alternative

Android documents [`postWebMessage`](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge) as an **alternative** asynchronous messaging mechanism. In the same comparison table it is marked **Recommended: No** and **Security: High (Origin-aware)**, which is stronger than `addJavascriptInterface` but not as strong as `addWebMessageListener`.

If this mechanism is used, configure a strict target origin and avoid wildcard targets such as `*`. Android's security guidance specifically warns that lack of origin control in `postWebMessage()` and `postMessage()` can allow attackers to intercept messages or send messages to native handlers.

## Minimize Exposed Native Functionality

Regardless of the bridge mechanism, minimize the native functionality exposed to JavaScript:

- expose only the specific operations the page needs
- avoid broad utility objects or generic command dispatchers
- do not expose sensitive capabilities unless they are essential
- require simple, well-defined message formats
- reject unexpected inputs and unsupported actions

## Scope and Limitations

This best practice is about bridge design and origin scoping. It should be combined with related controls for JavaScript enablement, trusted origin restrictions, and file access hardening. It does not by itself prevent attacker-controlled JavaScript from executing in a trusted page.
