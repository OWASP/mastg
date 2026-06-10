// Hook the location authorization and collection APIs at runtime.
// This script traces (1) the permission request that displays the
// NSLocationWhenInUseUsageDescription purpose string, (2) the start of GPS collection,
// and (3) the stop, which marks the end of the capture window.

console.log("\n[*] Starting Location Access Tracing...\n");

const printBacktrace = (context, maxLines = 8) => {
    console.log("\nBacktrace:");
    let backtrace = Thread.backtrace(context, Backtracer.ACCURATE)
        .map(DebugSymbol.fromAddress);
    for (let i = 0; i < Math.min(maxLines, backtrace.length); i++) {
        console.log(backtrace[i]);
    }
};

if (ObjC.available) {
    // CLLocationManager.requestWhenInUseAuthorization (instance method).
    // Displays NSLocationWhenInUseUsageDescription to the user when status is notDetermined.
    try {
        Interceptor.attach(ObjC.classes.CLLocationManager["- requestWhenInUseAuthorization"].implementation, {
            onEnter(args) {
                console.log("\n[+] CLLocationManager.requestWhenInUseAuthorization called");
                console.log("    Purpose string key: NSLocationWhenInUseUsageDescription");
                printBacktrace(this.context);
            }
        });
        console.log("[*] Hooked CLLocationManager.requestWhenInUseAuthorization");
    } catch (e) {
        console.log("[-] Failed to hook requestWhenInUseAuthorization: " + e);
    }

    // CLLocationManager.startUpdatingLocation (instance method).
    // Reaching this confirms the app actively collects GPS coordinates.
    try {
        Interceptor.attach(ObjC.classes.CLLocationManager["- startUpdatingLocation"].implementation, {
            onEnter(args) {
                console.log("\n[+] CLLocationManager.startUpdatingLocation called");
                console.log("    GPS coordinate collection has started.");
                printBacktrace(this.context);
            }
        });
        console.log("[*] Hooked CLLocationManager.startUpdatingLocation");
    } catch (e) {
        console.log("[-] Failed to hook startUpdatingLocation: " + e);
    }

    // CLLocationManager.stopUpdatingLocation (instance method).
    // Marks the end of the capture window (called when the countdown ends).
    try {
        Interceptor.attach(ObjC.classes.CLLocationManager["- stopUpdatingLocation"].implementation, {
            onEnter(args) {
                console.log("\n[+] CLLocationManager.stopUpdatingLocation called");
                console.log("    GPS coordinate collection has stopped.");
                printBacktrace(this.context);
            }
        });
        console.log("[*] Hooked CLLocationManager.stopUpdatingLocation");
    } catch (e) {
        console.log("[-] Failed to hook stopUpdatingLocation: " + e);
    }
}

console.log("\n[*] Hooks installed. Tap the Start button to trigger the countdown and location capture.\n");
