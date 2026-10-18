Java.perform(function() {
    console.log("[*] Hooking WebViewClient URL loading handlers...");

    var WebView = Java.use("android.webkit.WebView");
    var WebViewClient = Java.use("android.webkit.WebViewClient");
    var Uri = Java.use("android.net.Uri");

    // Per-thread bookkeeping so we can tell which URL-inspection calls happen
    // *while* one of the app's URL handlers is executing (handlers may run on
    // several threads concurrently).
    var handlerActive = {};   // threadId -> bool
    var inspectionLog = {};   // threadId -> array of method names

    function tid() { return Process.getCurrentThreadId(); }

    // A real allowlist/validation check reads the URL's host/scheme/path. We hook
    // those accessors and record any call made from inside a handler. If a handler
    // never calls them, it makes no host-based validation decision.
    ["getHost", "getScheme", "getPath"].forEach(function(name) {
        Uri[name].implementation = function() {
            var t = tid();
            if (handlerActive[t]) {
                inspectionLog[t].push(name);
            }
            return this[name]();
        };
    });

    function reportInspection(t) {
        var calls = inspectionLog[t] || [];
        if (calls.length === 0) {
            console.log("    URL inspection during handler: NONE (no host/scheme/path check -> no validation)");
        } else {
            console.log("    URL inspection during handler: " + calls.join(", "));
        }
    }

    // Reveal the WebViewClient the app registers and which handlers it overrides.
    WebView.setWebViewClient.implementation = function(client) {
        this.setWebViewClient(client);
        var cls = client.getClass();
        console.log("\n[*] setWebViewClient called");
        console.log("    WebViewClient implementation: " + cls.getName());
        var methods = cls.getDeclaredMethods();
        for (var i = 0; i < methods.length; i++) {
            var n = methods[i].getName();
            if (n === "shouldOverrideUrlLoading" || n === "shouldInterceptRequest") {
                console.log("    [!] Overrides " + n + " (custom URL handling)");
            }
        }
    };

    WebViewClient.shouldOverrideUrlLoading.overload('android.webkit.WebView', 'android.webkit.WebResourceRequest').implementation = function(view, request) {
        var t = tid();
        var url = request.getUrl().toString();
        handlerActive[t] = true;
        inspectionLog[t] = [];
        var result = this.shouldOverrideUrlLoading(view, request);
        handlerActive[t] = false;
        console.log("\n[shouldOverrideUrlLoading] URL: " + url);
        reportInspection(t);
        console.log("    -> returned " + result + " (false = URL loaded without validation)");
        return result;
    };

    WebViewClient.shouldInterceptRequest.overload('android.webkit.WebView', 'android.webkit.WebResourceRequest').implementation = function(view, request) {
        var t = tid();
        var url = request.getUrl().toString();
        handlerActive[t] = true;
        inspectionLog[t] = [];
        var result = this.shouldInterceptRequest(view, request);
        handlerActive[t] = false;
        console.log("\n[shouldInterceptRequest] URL: " + url);
        reportInspection(t);
        console.log("    -> " + (result != null ? "custom response returned" : "default loading (no validation)"));
        return result;
    };

    console.log("[*] WebViewClient hooks installed successfully");
});
