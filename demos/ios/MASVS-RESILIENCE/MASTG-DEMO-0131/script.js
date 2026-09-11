const BACKTRACE_LIMIT = 6;
const hookedAddresses = new Set();

function printBacktrace(context) {
    console.log("Backtrace:");
    const frames = Thread.backtrace(context, Backtracer.ACCURATE)
        .map(DebugSymbol.fromAddress)
        .slice(0, BACKTRACE_LIMIT);

    for (const frame of frames) {
        console.log(frame);
    }
}

function logEvent(message, context) {
    console.log(message);
    printBacktrace(context);
    console.log("");
}

function hookGlobalExport(symbolName, callbacks) {
    const address = Module.findGlobalExportByName(symbolName);
    if (address === null) {
        console.log(`[skip] ${symbolName} not found`);
        return;
    }

    const addressKey = address.toString();
    if (hookedAddresses.has(addressKey)) {
        return;
    }

    hookedAddresses.add(addressKey);
    Interceptor.attach(address, callbacks);
}

hookGlobalExport("MTLCreateSystemDefaultDevice", {
    onEnter(args) {
        this.contextCopy = this.context;
    },
    onLeave(retval) {
        const outcome = retval.isNull() ? "missing" : "available";
        logEvent(`MTLCreateSystemDefaultDevice() => ${outcome}`, this.contextCopy);
    }
});

hookGlobalExport("sysctlbyname", {
    onEnter(args) {
        this.contextCopy = this.context;
        this.name = args[0].readCString();
        this.outputPtr = args[1];
        this.shouldLog = this.name === "hw.machine" && !this.outputPtr.isNull();
    },
    onLeave(retval) {
        if (!this.shouldLog || retval.toInt32() !== 0) {
            return;
        }

        const value = this.outputPtr.readCString();
        logEvent(`sysctlbyname("hw.machine") => ${value}`, this.contextCopy);
    }
});

function hookStatSymbol(symbolName) {
    hookGlobalExport(symbolName, {
        onEnter(args) {
            this.contextCopy = this.context;
            this.path = args[0].readCString();
            this.shouldLog = this.path === "/usr/libexec/corelliumd";
        },
        onLeave(retval) {
            if (!this.shouldLog) {
                return;
            }

            const outcome = retval.toInt32() === 0 ? "present" : "missing";
            logEvent(`${symbolName}("/usr/libexec/corelliumd") => ${outcome}`, this.contextCopy);
        }
    });
}

hookStatSymbol("stat");
