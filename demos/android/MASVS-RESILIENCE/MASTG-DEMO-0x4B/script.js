Java.perform(function () {


    var XPOSED_PKG_PREFIXES = [
        "de.robv.android.xposed",
        "org.meowcat.edxposed",
        "io.va.exposed",
        "com.solohsu.android.edxp",
        "org.lsposed",
        "com.gauravssnl.bypassrootcheck",
        "com.w311ang.disable_flag_keep_screen_on"
    ];

    var STACK_NEEDLES = [
        "de.robv.android.xposed",
        "org.lsposed.lspd",
        "org.lsposed.",
        "lsphooker_",
        "lsplant",
        "edxposed",
        "re.frida"
    ];


    var BufferedReader = Java.use("java.io.BufferedReader");

    var readLine = BufferedReader.readLine.overload();
    readLine.implementation = function () {
        while (true) {
            var line = readLine.call(this);
            if (line === null) return null;
            var dirty = false;
            for (var i = 0; i < XPOSED_PKG_PREFIXES.length; i++) {
                if (line.indexOf(XPOSED_PKG_PREFIXES[i]) !== -1) { dirty = true; break; }
            }
            if (!dirty) return line;
            console.log("[bypass] dropping /proc/self/maps line: " + line);
        }
    };

    var Throwable = Java.use("java.lang.Throwable");
    var ThreadCls = Java.use("java.lang.Thread");

    function filterFrames(frames) {
        if (frames === null) return frames;
        var clean = [];
        for (var i = 0; i < frames.length; i++) {
            var name = (frames[i].getClassName() + "").toLowerCase();
            var dirty = false;
            for (var j = 0; j < STACK_NEEDLES.length; j++) {
                if (name.indexOf(STACK_NEEDLES[j].toLowerCase()) !== -1) { dirty = true; break; }
            }
            if (!dirty) clean.push(frames[i]);
        }
        if (clean.length === frames.length) return frames;
        console.log("[bypass] stripped " + (frames.length - clean.length) + " framework frames");
        return Java.array("java.lang.StackTraceElement", clean);
    }


    var throwableGetStackTrace = Throwable.getStackTrace.overload();
    throwableGetStackTrace.implementation = function () {
        return filterFrames(throwableGetStackTrace.call(this));
    };

    var threadGetStackTrace = ThreadCls.getStackTrace.overload();
    threadGetStackTrace.implementation = function () {
        return filterFrames(threadGetStackTrace.call(this));
    };

    var threadGetAllStackTraces = ThreadCls.getAllStackTraces.overload();
    threadGetAllStackTraces.implementation = function () {
        var raw = threadGetAllStackTraces.call(this);
        var HashMap = Java.use("java.util.HashMap");
        var clean = HashMap.$new();
        var keys = raw.keySet().toArray();
        for (var i = 0; i < keys.length; i++) {
            try {
                var t = Java.cast(keys[i], ThreadCls);
                clean.put(t, filterFrames(threadGetStackTrace.call(t)));
            } catch (e) { }
        }
        return clean;
    };

    Process.setExceptionHandler(function (details) {
        console.log("[bypass] swallowed native exception: " + JSON.stringify(details));
        return true;
    });

    console.log("[bypass] Demo 4 hooks installed (/proc/self/maps + stack-trace probes).");
});
