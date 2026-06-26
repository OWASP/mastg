Java.perform(function() {
    console.log("[*] Hooking WebViewClient methods...");

    var WebViewClient = Java.use("android.webkit.WebViewClient");
    var Uri = Java.use("android.net.Uri");

    // Hook shouldOverrideUrlLoading (API 24+)
    WebViewClient.shouldOverrideUrlLoading.overload('android.webkit.WebView', 'android.webkit.WebResourceRequest').implementation = function(view, request) {
        var url = request.getUrl().toString();
        console.log("\n[shouldOverrideUrlLoading] URL: " + url);
        
        var result = this.shouldOverrideUrlLoading(view, request);
        console.log("[shouldOverrideUrlLoading] Return value: " + result + " (false = load URL, true = block URL)");
        
        // Log URL components
        var uri = request.getUrl();
        console.log("  Scheme: " + uri.getScheme());
        console.log("  Host: " + uri.getHost());
        console.log("  Path: " + uri.getPath());
        
        return result;
    };

    // Hook shouldInterceptRequest (API 21+)
    WebViewClient.shouldInterceptRequest.overload('android.webkit.WebView', 'android.webkit.WebResourceRequest').implementation = function(view, request) {
        var url = request.getUrl().toString();
        console.log("\n[shouldInterceptRequest] URL: " + url);
        
        var result = this.shouldInterceptRequest(view, request);
        if (result != null) {
            console.log("[shouldInterceptRequest] Custom response returned");
        } else {
            console.log("[shouldInterceptRequest] Default loading behavior");
        }
        
        return result;
    };

    // Hook Uri parsing methods to see validation logic
    Uri.getHost.implementation = function() {
        var result = this.getHost();
        console.log("[Uri.getHost] " + result);
        return result;
    };

    Uri.getScheme.implementation = function() {
        var result = this.getScheme();
        console.log("[Uri.getScheme] " + result);
        return result;
    };

    Uri.getPath.implementation = function() {
        var result = this.getPath();
        console.log("[Uri.getPath] " + result);
        return result;
    };

    console.log("[*] WebViewClient hooks installed successfully");
});
