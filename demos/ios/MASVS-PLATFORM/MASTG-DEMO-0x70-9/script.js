if (ObjC.available) {
    console.log("[*] Starting Frida dynamic tracing for Outgoing Links...");
    var UIApplication = ObjC.classes.UIApplication;
    if (UIApplication) {
        var openUrlMethod = UIApplication["- openURL:options:completionHandler:"];
        if (openUrlMethod) {
            Interceptor.attach(openUrlMethod.implementation, {
                onEnter: function(args) {
                    var url = new ObjC.Object(args[2]);
                    console.log("[!] DYNAMIC ALERT (Outgoing): App is executing an outgoing link:");
                    console.log("    -> " + url.absoluteString());
                }
            });
        }
    }
} else {
    console.log("[-] Objective-C runtime is not available.");
}