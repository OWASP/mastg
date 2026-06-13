// frida -U -f org.owasp.mastestapp -l script.js

Java.perform(function () {


    const THREAD_KEYWORDS = [
        "gum-js-loop",
        "gmain",
        "gdbus",
        "pool-frida",
        "frida"
    ];


    const MAPS_KEYWORDS = [
        "frida-agent",
        "libfrida",
        "frida-gadget",
        "gum-js-loop",
        "linjector",
        "/gum"
    ];

    const File = Java.use("java.io.File");
    const FileInputStream = Java.use("java.io.FileInputStream");
    const BufferedReader = Java.use("java.io.BufferedReader");
    const Socket = Java.use("java.net.Socket");
    const InetSocketAddress = Java.use("java.net.InetSocketAddress");
    const ConnectException = Java.use("java.net.ConnectException");


    const socketConnect = Socket.connect.overload("java.net.SocketAddress", "int");
    socketConnect.implementation = function (endpoint, timeout) {
        const addr = Java.cast(endpoint, InetSocketAddress);
        if (addr.getPort() === 27042) {
            console.log("[+] Blocked Frida default-port probe (127.0.0.1:27042)");
            throw ConnectException.$new("Connection refused");
        }
        return socketConnect.call(this, endpoint, timeout);
    };


    function commName(tidDir) {
        try {
            const comm = File.$new(tidDir, "comm");
            if (!comm.canRead()) return "";
            const fis = FileInputStream.$new(comm);
            const buffer = Java.array("byte", new Array(64).fill(0));
            const size = fis.read(buffer);
            fis.close();
            if (size <= 0) return "";
            let name = "";
            for (let j = 0; j < size; j++) {
                name += String.fromCharCode(buffer[j] & 0xff);
            }
            return name.trim();
        } catch (e) {
            return "";
        }
    }

    function filterTaskEntries(tids) {
        const kept = [];
        for (let i = 0; i < tids.length; i++) {
            const name = commName(tids[i]);
            let hide = false;
            for (let k = 0; k < THREAD_KEYWORDS.length; k++) {
                if (name.indexOf(THREAD_KEYWORDS[k]) !== -1) { hide = true; break; }
            }
            if (hide) {
                console.log("[+] Hiding Frida thread: " + name);
                continue;
            }
            kept.push(tids[i]);
        }
        return Java.array("java.io.File", kept);
    }

    let taskListLogged = false;
    function noteTaskIntercepted() {
        if (!taskListLogged) {
            taskListLogged = true;
            console.log("[+] File.listFiles(/proc/self/task) intercepted (thread-enumeration bypass active)");
        }
    }

    const listFilesFilter = File.listFiles.overload("java.io.FileFilter");
    listFilesFilter.implementation = function (filter) {
        const files = listFilesFilter.call(this, filter);
        if (files === null) return files;
        if (this.getAbsolutePath() !== "/proc/self/task") return files;
        noteTaskIntercepted();
        return filterTaskEntries(files);
    };

    const listFilesNoArg = File.listFiles.overload();
    listFilesNoArg.implementation = function () {
        const files = listFilesNoArg.call(this);
        if (files === null) return files;
        if (this.getAbsolutePath() !== "/proc/self/task") return files;
        noteTaskIntercepted();
        return filterTaskEntries(files);
    };


    const readLine = BufferedReader.readLine.overload();
    readLine.implementation = function () {
        while (true) {
            const line = readLine.call(this);
            if (line === null) return null;
            let suspicious = false;
            for (let i = 0; i < MAPS_KEYWORDS.length; i++) {
                if (line.indexOf(MAPS_KEYWORDS[i]) !== -1) { suspicious = true; break; }
            }
            if (!suspicious) return line;
            console.log("[+] Removed maps entry: " + line);
        }
    };

    console.log("[+] Frida detection bypass hooks installed");
});
