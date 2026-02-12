Java.perform(() => {

    // --- Bypass: Hook BufferedReader.readLine() to hide Frida from /proc/self/maps ---

    let BufferedReader = Java.use("java.io.BufferedReader");

    BufferedReader["readLine"].overload().implementation = function () {
        let line = this["readLine"]();
        while (line !== null) {
            let lower = line.toString().toLowerCase();
            if (lower.indexOf("frida") !== -1 || lower.indexOf("gadget") !== -1) {
                console.log("[*] Hiding line from /proc/self/maps: " + line);
                line = this["readLine"]();
            } else {
                break;
            }
        }
        return line;
    };

    console.log("[+] BufferedReader.readLine() hooked: filtering Frida-related entries from /proc/self/maps");

    // --- Hook Cipher.doFinal() to extract sensitive data ---

    function printBacktrace(maxLines = 8) {
        let Exception = Java.use("java.lang.Exception");
        let stackTrace = Exception.$new().getStackTrace().toString().split(",");

        console.log("\nBacktrace:");
        for (let i = 0; i < Math.min(maxLines, stackTrace.length); i++) {
            console.log(stackTrace[i]);
        }
    }

    function bytesToHex(bytes) {
        let hex = [];
        for (let i = 0; i < bytes.length; i++) {
            hex.push(("0" + (bytes[i] & 0xFF).toString(16)).slice(-2));
        }
        return hex.join("");
    }

    let Cipher = Java.use("javax.crypto.Cipher");
    let StringClass = Java.use("java.lang.String");

    // Hook Cipher.doFinal(byte[])
    Cipher["doFinal"].overload("[B").implementation = function (input) {
        let result = this["doFinal"](input);
        let algorithm = this.getAlgorithm();

        // Cipher.ENCRYPT_MODE = 1, Cipher.DECRYPT_MODE = 2
        let opmode = this.opmode.value;
        let modeStr = opmode === 1 ? "ENCRYPT_MODE" : opmode === 2 ? "DECRYPT_MODE" : "UNKNOWN(" + opmode + ")";

        console.log("\n\n[*] Cipher.doFinal(byte[]) called");
        console.log("    Algorithm: " + algorithm);
        console.log("    Mode: " + modeStr);

        if (opmode === 1) {
            try {
                console.log("    Input (plaintext): " + StringClass.$new(input));
            } catch (e) {
                console.log("    Input (hex): " + bytesToHex(input));
            }
            console.log("    Output (ciphertext hex): " + bytesToHex(result));
        }

        if (opmode === 2) {
            console.log("    Input (ciphertext hex): " + bytesToHex(input));
            try {
                console.log("    Output (plaintext): " + StringClass.$new(result));
            } catch (e) {
                console.log("    Output (hex): " + bytesToHex(result));
            }
        }

        printBacktrace();

        return result;
    };

    console.log("[+] Cipher.doFinal() hooked: extracting sensitive data\n");
});