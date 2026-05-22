Java.perform(function () {

    const PROC_KEYWORDS = [
        "frida-server",
        "frida-agent",
        "gum-js-loop",
        "gmain"
    ];

    const MAPS_KEYWORDS = [
        "frida",
        "gum-js-loop",
        "libfrida"
    ];

    const File = Java.use("java.io.File");
    const FileInputStream = Java.use("java.io.FileInputStream");
    const BufferedReader = Java.use("java.io.BufferedReader");
    const Socket = Java.use("java.net.Socket");
    const InetSocketAddress = Java.use("java.net.InetSocketAddress");
    const ConnectException = Java.use("java.net.ConnectException");

const socketConnect =
    Socket.connect.overload("java.net.SocketAddress", "int");

socketConnect.implementation = function (endpoint, timeout) {

    const addr = Java.cast(endpoint, InetSocketAddress);
    const port = addr.getPort();

    if (port === 27042) {

        console.log("[+] Blocked Frida port probe");

        throw ConnectException.$new("Connection refused");
    }

    return socketConnect.call(this, endpoint, timeout);
};

    let procListFilesLogged = false;

    function filterProcEntries(files) {
        let filtered = [];
        for (let i = 0; i < files.length; i++) {
            try {
                const cmdline = File.$new(files[i], "cmdline");
                const fis = FileInputStream.$new(cmdline);
                const buffer = Java.array("byte", new Array(256).fill(0));
                const size = fis.read(buffer);
                fis.close();
                if (size > 0) {
                    let processName = "";
                    for (let j = 0; j < size; j++) {
                        processName += String.fromCharCode(buffer[j] & 0xff);
                    }
                    let hide = false;
                    for (let k = 0; k < PROC_KEYWORDS.length; k++) {
                        if (processName.indexOf(PROC_KEYWORDS[k]) !== -1) {
                            hide = true;
                            break;
                        }
                    }
                    if (hide) {
                        console.log("[+] Hiding process: " + processName);
                        continue;
                    }
                }
            } catch (e) {}
            filtered.push(files[i]);
        }
        return Java.array("java.io.File", filtered);
    }

    function noteProcIntercepted() {
        if (!procListFilesLogged) {
            procListFilesLogged = true;
            console.log("[+] File.listFiles(/proc) intercepted (process-enumeration bypass active)");
        }
    }

    const listFilesNoArg = File.listFiles.overload();
    listFilesNoArg.implementation = function () {
        const files = listFilesNoArg.call(this);
        if (files === null) return files;
        if (this.getAbsolutePath() !== "/proc") return files;
        noteProcIntercepted();
        return filterProcEntries(files);
    };

    const listFilesFilter = File.listFiles.overload("java.io.FileFilter");
    listFilesFilter.implementation = function (filter) {
        const files = listFilesFilter.call(this, filter);
        if (files === null) return files;
        if (this.getAbsolutePath() !== "/proc") return files;
        noteProcIntercepted();
        return filterProcEntries(files);
    };


    const readLine =
        BufferedReader.readLine.overload();

    readLine.implementation = function () {

        while (true) {

            const line = readLine.call(this);

            if (line === null) {
                return null;
            }

            let suspicious = false;

            for (let i = 0; i < MAPS_KEYWORDS.length; i++) {

                if (line.indexOf(MAPS_KEYWORDS[i]) !== -1) {
                    suspicious = true;
                    break;
                }
            }

            if (!suspicious) {
                return line;
            }

            console.log("[+] Removed maps entry: " + line);
        }
    };


    console.log("[+] Frida detection bypass hooks installed");
});