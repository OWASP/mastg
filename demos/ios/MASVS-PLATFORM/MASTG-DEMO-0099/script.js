if (ObjC.available) {
    // 1. Hooking Simple Setters (Booleans)
    // We can use simpler swizzling here for stability
    const WKPreferences = ObjC.classes.WKPreferences;
    const WKWebViewConfiguration = ObjC.classes.WKWebViewConfiguration;
    const WKWebView = ObjC.classes.WKWebView;

    // Helper to hook boolean setters
    function hookBoolMethod(klass, selector, label) {
        if (!klass) {
            console.log(`[!] Skipping ${label}, class is unavailable.`);
            return;
        }

        const method = klass[selector];
        if (!method || !method.implementation) {
            console.log(`[!] Skipping ${label}, selector ${selector} is unavailable.`);
            return;
        }

        const oldImpl = method.implementation;

        method.implementation = ObjC.implement(method, function (self, sel, value) {
            console.log(`[${label}] = ${value}`);
            oldImpl(self, sel, value);
        });
    }

    hookBoolMethod(WKPreferences, "- _setAllowFileAccessFromFileURLs:", "WKPreferences: allowFileAccess");
    hookBoolMethod(WKWebViewConfiguration, "- _setAllowUniversalAccessFromFileURLs:", "WKWebViewConfig: universalAccess");
    hookBoolMethod(WKPreferences, "- setJavaScriptEnabled:", "WKPreferences: jsEnabled");

    // 2. Hooking loadFileURL (The tricky one)
    if (!WKWebView) {
        console.log("[!] Skipping WKWebView loadFileURL hook, class is unavailable.");
    } else {
        const loadMethod = WKWebView["- loadFileURL:allowingReadAccessToURL:"];
        if (!loadMethod || !loadMethod.implementation) {
            console.log("[!] Skipping WKWebView loadFileURL hook, selector is unavailable.");
        } else {
            const oldLoadImpl = loadMethod.implementation;

            loadMethod.implementation = ObjC.implement(loadMethod, function (self, sel, fileURLPtr, readAccessPtr) {
                try {
                    // In ObjC.implement, Frida automatically wraps pointers as ObjC.Object if possible,
                    // but we use ObjC.Object() here to ensure we can call methods on them.
                    const fileURL = new ObjC.Object(fileURLPtr);
                    const readAccess = new ObjC.Object(readAccessPtr);

                    console.log("[WKWebView] loadFileURL called");
                    // Use absoluteString safely
                    console.log("  fileURL: " + fileURL.absoluteString().toString());
                    console.log("  allowingReadAccessTo: " + readAccess.absoluteString().toString());
                } catch (e) {
                    console.log("[!] Error reading URLs: " + e);
                }

                // Always call the original implementation and return its value
                return oldLoadImpl(self, sel, fileURLPtr, readAccessPtr);
            });
        }
    }

    console.log("[+] Hooks deployed successfully.");

} else {
    console.log("Objective-C runtime is not available.");
}