// Frida script to detect and bypass common root detection mechanisms
// This script demonstrates what root detection checks the app performs

console.log("[*] Root Detection Bypass Script Started");
console.log("[*] Monitoring common root detection methods...\n");

Java.perform(function() {
    
    // Hook File.exists() to detect file-based checks
    var File = Java.use("java.io.File");
    var originalExists = File.exists;
    File.exists.implementation = function() {
        var path = this.getAbsolutePath();
        var result = originalExists.call(this);
        
        // Check if it's a root-related path
        if (path.indexOf("/su") !== -1 || 
            path.indexOf("superuser") !== -1 || 
            path.indexOf("Superuser") !== -1 ||
            path.indexOf("magisk") !== -1 ||
            path.indexOf("Magisk") !== -1) {
            console.log("[!] ROOT CHECK DETECTED - File.exists()");
            console.log("    Path: " + path);
            console.log("    Original result: " + result);
            console.log("    Returning: false (bypassed)\n");
            return false;  // Bypass by returning false
        }
        return result;
    };
    
    // Hook PackageManager.getPackageInfo() to detect package checks
    try {
        var PackageManager = Java.use("android.content.pm.PackageManager");
        var originalGetPackageInfo = PackageManager.getPackageInfo.overload('java.lang.String', 'int');
        originalGetPackageInfo.implementation = function(packageName, flags) {
            var rootPackages = [
                "com.noshufou.android.su",
                "com.noshufou.android.su.elite",
                "eu.chainfire.supersu",
                "com.koushikdutta.superuser",
                "com.topjohnwu.magisk",
                "com.kingroot.kinguser"
            ];
            
            if (rootPackages.indexOf(packageName) !== -1) {
                console.log("[!] ROOT CHECK DETECTED - PackageManager.getPackageInfo()");
                console.log("    Package: " + packageName);
                console.log("    Throwing NameNotFoundException (bypassed)\n");
                throw Java.use("android.content.pm.PackageManager$NameNotFoundException").$new();
            }
            return originalGetPackageInfo.call(this, packageName, flags);
        };
    } catch (err) {
        console.log("[-] Could not hook PackageManager.getPackageInfo: " + err);
    }
    
    // Hook Build.TAGS to detect test-keys checks
    try {
        var Build = Java.use("android.os.Build");
        var originalTags = Build.TAGS.value;
        
        // Monitor when TAGS is accessed
        Object.defineProperty(Build, "TAGS", {
            get: function() {
                if (originalTags && originalTags.indexOf("test-keys") !== -1) {
                    console.log("[!] ROOT CHECK DETECTED - Build.TAGS");
                    console.log("    Original value: " + originalTags);
                    console.log("    Returning: release-keys (bypassed)\n");
                    return "release-keys";
                }
                return originalTags;
            }
        });
    } catch (err) {
        console.log("[-] Could not hook Build.TAGS: " + err);
    }
    
    // Hook Runtime.exec() to detect su execution attempts
    try {
        var Runtime = Java.use("java.lang.Runtime");
        var originalExec = Runtime.exec.overload('java.lang.String');
        originalExec.implementation = function(cmd) {
            if (cmd.indexOf("su") !== -1 || cmd.indexOf("getprop") !== -1) {
                console.log("[!] ROOT CHECK DETECTED - Runtime.exec()");
                console.log("    Command: " + cmd);
                console.log("    Allowing execution but monitoring...\n");
            }
            return originalExec.call(this, cmd);
        };
    } catch (err) {
        console.log("[-] Could not hook Runtime.exec: " + err);
    }
    
    // Hook common root detection libraries - RootBeer
    try {
        var RootBeer = Java.use("com.scottyab.rootbeer.RootBeer");
        RootBeer.isRooted.implementation = function() {
            console.log("[!] ROOT CHECK DETECTED - RootBeer.isRooted()");
            console.log("    Returning: false (bypassed)\n");
            return false;
        };
    } catch (err) {
        // RootBeer library not present
    }
    
    console.log("[*] Root detection monitoring active. Interact with the app...\n");
});
