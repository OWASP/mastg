if (ObjC.available) {
    console.log("[*] Starting Frida dynamic tracing for Universal Link Receiver...");
    var NSUserActivity = ObjC.classes.NSUserActivity;
    if (NSUserActivity) {
        var webpageURLMethod = NSUserActivity["- webpageURL"];
        if (webpageURLMethod) {
            Interceptor.attach(webpageURLMethod.implementation, {
                onLeave: function(retval) {
                    if (!retval.isNull()) {
                        var url = new ObjC.Object(retval);
                        console.log("[!] DYNAMIC ALERT (Receiver): App is reading incoming Universal Link:");
                        console.log("    -> " + url.absoluteString());
                    }
                }
            });
        }
    }
} else {
    console.log("[-] Objective-C runtime is not available.");
}