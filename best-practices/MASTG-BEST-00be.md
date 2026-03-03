---
title: Ensure WebViews Within Organizational Trust Boundaries
alias: ensure-webviews-within-organizational-trust-boundaries
id: MASTG-BEST-00be
platform: android
knowledge: [ MASTG-KNOW-0018 ]
---

## Recommendation

WebViews in Android allow applications to render web content, but they can introduce significant security risks if not properly managed.

Whenever possible, follow the guidance in @MASTG-BEST-0012.

Additionally, load only static WebViews packaged within the app bundle, and do not load resources or link (redirect) to external domains. This approach ensures that the displayed content cannot be tampered with remotely.

If your application must display dynamic web content from the internet, ensure that all websites loaded in your WebView are secure and under your organization's control (or at least within your organization's trust boundaries).

When you need to load partial resources (especially JavaScript files) or even full websites outside your organization's trust boundaries, do not load them directly into a WebView. Instead, open the website in the user's default browser or use safer alternatives such as [Trusted Web Activities](https://developer.android.com/guide/topics/app-bundle/trusted-web-activities) or [Custom Tabs](https://developer.chrome.com/docs/android/custom-tabs/overview/). These solutions leverage the browser's isolated environment.

To enforce domain control and prevent untrusted content from loading inside your app, apply a control like the following:

```kotlin
webView.webViewClient = object : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()
        Log.d("WebView", "About to load: $url")

        // You can intercept or allow it:
        val outsideControl = isOutsideControl(url)
        if(outsideControl){
            // Handle the case where the URL is outside your control
            // For example, open it in the default browser instead
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
        return outsideControl // return true if you want to handle it manually
    }
}

fun isOutsideControl(url: String): Boolean {
    val trustedSchemes = setOf("https")
    val trustedHosts = setOf("my-domain.com", "another-trusted-domain.com")
    val uri = Uri.parse(url)

    val scheme = uri.scheme ?: return true
    val host = uri.host ?: return true

    if (scheme !in trustedSchemes) {
       return true
    }
    return host !in trustedHosts
}
```
