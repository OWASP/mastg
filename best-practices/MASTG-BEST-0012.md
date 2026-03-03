---
title: Disable JavaScript in WebViews
alias: disable-javascript-in-webviews
id: MASTG-BEST-0012
platform: android
knowledge: [MASTG-KNOW-0018]
---

If JavaScript is **not required**, explicitly disable it in WebViews by setting [`setJavaScriptEnabled(false)`](https://developer.android.com/reference/android/webkit/WebSettings.html#setJavaScriptEnabled%28boolean%29).

Enabling JavaScript in WebViews **increases the attack surface** and can expose your app to severe security risks.

Often, this is not possible due to the app's functional requirements. In those cases, ensure that you have implemented proper input validation, output encoding, and other security measures when JavaScript is enabled in WebViews. Consider @MASTG-BEST-00be as a compensating control.

!!! note
    As an alternative to regular WebViews, you should also consider options such as [Trusted Web Activities](https://developer.android.com/develop/ui/views/layout/webapps/trusted-web-activities) or [Custom Tabs](https://developer.chrome.com/docs/android/custom-tabs/overview/).
