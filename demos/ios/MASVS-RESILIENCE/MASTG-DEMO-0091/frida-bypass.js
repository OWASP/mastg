// MASTG-DEMO-0091: Frida Detection Bypass
// This script demonstrates bypassing the detection mechanisms

console.log("[*] MASTG-DEMO-0091: Frida Detection Bypass");
console.log("=".repeat(50));

// Bypass 1: Hide Frida libraries from dyld
console.log("[+] Bypass 1: Hiding Frida libraries");

Interceptor.attach(Module.findExportByName(null, '_dyld_get_image_name'), {
    onLeave: function(retval) {
        if (retval.isNull()) return;
        
        try {
            var imageName = retval.readCString();
            if (imageName && imageName.toLowerCase().includes('frida')) {
                console.log("    [!] Hiding: " + imageName);
                retval.replace(Memory.allocUtf8String('/System/Library/Frameworks/Foundation.framework/Foundation'));
            }
        } catch(e) {}
    }
});

// Bypass 2: Block port detection
console.log("[+] Bypass 2: Blocking Frida port detection");

Interceptor.attach(Module.findExportByName(null, 'connect'), {
    onEnter: function(args) {
        try {
            var sockaddr = args[1];
            var port = Memory.readU16(sockaddr.add(2));
            port = ((port & 0xFF) << 8) | ((port >> 8) & 0xFF);
            
            if (port === 27042 || port === 27043) {
                console.log("    [!] Blocked port check: " + port);
                this.shouldFail = true;
            }
        } catch(e) {}
    },
    onLeave: function(retval) {
        if (this.shouldFail) {
            retval.replace(-1);
        }
    }
});

// Bypass 3: Normalize thread count
console.log("[+] Bypass 3: Normalizing thread count");

Interceptor.attach(Module.findExportByName(null, 'task_threads'), {
    onLeave: function(retval) {
        try {
            if (retval.toInt32() === 0) {
                var threadCountPtr = this.context.rdx; // x86_64
                var actualCount = Memory.readU32(threadCountPtr);
                
                if (actualCount > 12) {
                    console.log("    [!] Reducing thread count from " + actualCount + " to 8");
                    Memory.writeU32(threadCountPtr, 8);
                }
            }
        } catch(e) {}
    }
});

// Bypass 4: Hook mastgTest directly
console.log("[+] Bypass 4: Hooking mastgTest()");

if (ObjC.available) {
    try {
        var MastgTest = ObjC.classes.MastgTest;
        
        if (MastgTest && MastgTest['+ mastgTest']) {
            Interceptor.attach(MastgTest['+ mastgTest'].implementation, {
                onLeave: function(retval) {
                    console.log("    [!] mastgTest() intercepted");
                    var cleanResult = ObjC.classes.NSString.stringWithString_(
                        "✅ No Frida detected - App is running normally"
                    );
                    retval.replace(cleanResult);
                    console.log("    [✓] Returning clean result");
                }
            });
            console.log("    [✓] mastgTest() hooked successfully");
        }
    } catch(e) {
        console.log("    [!] Error: " + e);
    }
}

console.log("\n" + "=".repeat(50));
console.log("[✓] All bypasses active!");
console.log("=".repeat(50) + "\n");
