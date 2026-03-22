---
title: Disable JavaScript in WebViews
alias: disable-javascript-in-webviews
id: MASTG-BEST-0012
platform: android
knowledge: [MASTG-KNOW-0018]
---

Enabling JavaScript is **not a vulnerability by itself**. In real apps it is often required for legitimate functionality, such as rendering modern web applications, interactive account portals, support centers, payment or login flows, or hybrid app content built with web technologies. Frameworks such as Ionic and Capacitor are built around a WebView that runs JavaScript application code, and `react-native-webview` exists specifically to render web content in a native view.

Android's guidance associates unsafe use of JavaScript enabled WebViews with [cross-app scripting](https://developer.android.com/privacy-and-security/risks/cross-app-scripting). JavaScript does increase the attack surface of a WebView, but severe cases typically happen when it is combined with one or more of these conditions, loading untrusted or weakly validated content, exposed JavaScript bridges, permissive file or content access, or unsafe URL loading.

## Keep JavaScript Disabled in WebViews When Not Required

JavaScript is [disabled by default in WebViews](https://developer.android.com/develop/ui/views/layout/webapps/webview#EnablingJavaScript). If JavaScript is not required, do not enable it in the first place or [explicitly disable it](https://developer.android.com/privacy-and-security/risks/cross-app-scripting#cross-app-scripting-disable-javascript) in WebViews with [`setJavaScriptEnabled(false)`](https://developer.android.com/reference/android/webkit/WebSettings.html#setJavaScriptEnabled%28boolean%29).

- Keep JavaScript disabled for WebViews that only display static or minimally interactive content. Good candidates include static help pages, legal text, release notes, or other controlled content that does not need client side scripting.
- Only enable JavaScript when the WebView is intentionally used to run trusted web application logic. Good candidates include hybrid app screens, complex internal web apps, single page applications, and web based user experiences that depend on JavaScript to render or function.

## Use Alternatives to WebViews for External Content When Feasible

If you only need to open external web content, consider using [Custom Tabs](https://developer.chrome.com/docs/android/custom-tabs/overview/) instead of embedding a WebView. If you are shipping a web app you control, [Trusted Web Activities](https://developer.android.com/develop/ui/views/layout/webapps/trusted-web-activities) may also be appropriate. These options move rendering into the browser context rather than your app's WebView, which can reduce app specific WebView risk. They do not remove the need to secure the web content itself.

Custom Tabs are especially appropriate for authentication and other browser based flows because Android recommends them for sign in, noting that the host app cannot inspect the content. Trusted Web Activities also prevent the host app from having direct access to web content or web state such as cookies and localStorage.

## Hardening WebViews When JavaScript Is Required

If JavaScript is required, apply WebView specific hardening measures, covered in related MASTG best practices, to mitigate the increased attack surface. This includes, but is not limited to:

- only load expected and allowlisted origins
- validate scheme and host before calling `loadUrl`, `shouldOverrideUrlLoading`, or similar APIs
- disable file and content access unless they are strictly needed (@MASTG-BEST-0011 and @MASTG-BEST-0013)
- avoid exposing JavaScript bridges to untrusted content (@MASTG-BEST-00be)
- enable Safe Browsing
