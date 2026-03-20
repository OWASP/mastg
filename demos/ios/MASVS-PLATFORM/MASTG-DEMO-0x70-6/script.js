if (ObjC.available) {
    
    ObjC.schedule(ObjC.mainQueue, function () {
        console.log("[*] Triggering URL via Frida (V1 Methodology)...");
        
        var UIApplication = ObjC.classes.UIApplication.sharedApplication();
        
        var targetUrl = "https://attacker.example.com/reset_password?token=malicious_123";
        var toOpen = ObjC.classes.NSURL.URLWithString_(targetUrl);
        var result = UIApplication.openURL_(toOpen);
        
        console.log("[+] UIApplication.openURL_ executed.");
        console.log("[+] Target: " + targetUrl);
        console.log("[+] Result: " + result);
    });
} else {
    console.log("[-] Objective-C runtime is not available.");
}