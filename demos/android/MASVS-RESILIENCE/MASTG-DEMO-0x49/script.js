// frida -U -f org.owasp.mastestapp -l bypass.js

Java.perform(function () {

    const ArrayList = Java.use("java.util.ArrayList");
    const MastgTest = Java.use("org.owasp.mastestapp.MastgTest");

    console.log("[*] Installing Frida detection bypass hooks");


    MastgTest.checkFridaDefaultPort.implementation = function () {

        console.log("\n[+] checkFridaDefaultPort() intercepted");

        console.log(
            "- The port probe for 127.0.0.1:27042 was bypassed " +
            "by forcing the method to return false."
        );

        console.log(
            "- This prevents the application from detecting " +
            "the default frida-server listener."
        );

        return false;
    };


    MastgTest.checkFridaThreads.implementation = function () {

        console.log("\n[+] checkFridaThreads() intercepted");

        console.log(
            "- The /proc/self/task enumeration was bypassed " +
            "by returning an empty thread list."
        );

        console.log(
            "- Frida-related thread names such as " +
            "`gum-js-loop`, `gmain`, `gdbus`, and `pool-frida` " +
            "are hidden from the application."
        );

        return ArrayList.$new();
    };



    MastgTest.checkFridaLibraries.implementation = function () {

        console.log("\n[+] checkFridaLibraries() intercepted");

        console.log(
            "- The /proc/self/maps scan was bypassed " +
            "by returning an empty library match list."
        );

        console.log(
            "- Frida artifacts such as `frida-agent`, " +
            "`libfrida`, `frida-gadget`, `gum-js-loop`, " +
            "and `linjector` are hidden from detection."
        );

        return ArrayList.$new();
    };

    MastgTest.promptUserForLiability.implementation = function (msg) {

        console.log("\n[+] promptUserForLiability() intercepted");

        console.log(
            "- The anti-tampering warning dialog was suppressed " +
            "to prevent the user prompt from appearing."
        );

        console.log("- Dialog message:");
        console.log("  " + msg);

        return;
    };

    console.log("\n[*] All Frida detection hooks installed");
});