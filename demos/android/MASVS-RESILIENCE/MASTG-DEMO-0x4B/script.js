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
        "re.frida",
        "gum-js"
    ];

    var THREAD_NAME_NEEDLES = [
        "gum-js", "gmain", "pool-frida", "frida", "linjector",
        "lspd", "xposed", "lsphooker"
    ];

    var KNOWN_MANAGER_PACKAGES = [
        "de.robv.android.xposed.installer",
        "org.meowcat.edxposed.manager",
        "io.va.exposed",
        "com.solohsu.android.edxp.manager",
        "org.lsposed.manager"
    ];


    var SUSPICIOUS_NATIVE_METHODS = {
        "java.lang.System.currentTimeMillis": true,
        "java.lang.System.nanoTime": true,
        "java.lang.Object.notify": true,
        "java.lang.Object.notifyAll": true,
        "java.lang.Thread.currentThread": true
    };


    var APM = Java.use("android.app.ApplicationPackageManager");
    var NameNotFoundException = Java.use("android.content.pm.PackageManager$NameNotFoundException");

    APM.getPackageInfo.overload("java.lang.String", "int").implementation = function (name, flags) {
        for (var i = 0; i < KNOWN_MANAGER_PACKAGES.length; i++) {
            if (name === KNOWN_MANAGER_PACKAGES[i]) {
                console.log("[bypass] faking NameNotFoundException for " + name);
                throw NameNotFoundException.$new(name);
            }
        }
        return this.getPackageInfo(name, flags);
    };


    var Modifier = Java.use("java.lang.reflect.Modifier");
    var NATIVE_BIT = Modifier.NATIVE.value;
    var Method = Java.use("java.lang.reflect.Method");

    Method.getModifiers.implementation = function () {
        var orig = this.getModifiers();
        try {
            var key = this.getDeclaringClass().getName() + "." + this.getName();
            if (SUSPICIOUS_NATIVE_METHODS[key] && (orig & NATIVE_BIT) === 0) {
                console.log("[bypass] restoring Modifier.NATIVE on " + key);
                return orig | NATIVE_BIT;
            }
        } catch (e) { /* fall through */ }
        return orig;
    };


    var BufferedReader = Java.use("java.io.BufferedReader");
    BufferedReader.readLine.overload().implementation = function () {
        while (true) {
            var line = this.readLine();
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

    Throwable.getStackTrace.implementation = function () {
        return filterFrames(this.getStackTrace());
    };
    ThreadCls.getStackTrace.implementation = function () {
        return filterFrames(this.getStackTrace());
    };

    ThreadCls.getAllStackTraces.implementation = function () {
 
        var raw = this.getAllStackTraces();
        var HashMap = Java.use("java.util.HashMap");
        var clean = HashMap.$new();
        var keys = raw.keySet().toArray();
        for (var i = 0; i < keys.length; i++) {
            var t = keys[i];
            try {
                clean.put(t, t.getStackTrace());
            } catch (e) { /* thread died between snapshot and re-walk — skip */ }
        }
        return clean;
    };

    var FileCls = Java.use("java.io.File");
    var FIS = Java.use("java.io.FileInputStream");

    function readComm(tidDir) {
        try {
            var fis = FIS.$new(tidDir.getAbsolutePath() + "/comm");
            var buf = Java.array("byte", new Array(64).fill(0));
            var n = fis.read(buf);
            fis.close();
            if (n <= 0) return "";
            var s = "";
            for (var i = 0; i < n; i++) s += String.fromCharCode(buf[i] & 0xff);
            return s.trim();
        } catch (e) { return ""; }
    }

    FileCls.listFiles.overload().implementation = function () {
        var files = this.listFiles();
        if (files === null) return files;
        var path = this.getAbsolutePath();
        if (path !== "/proc/self/task") return files;
        var keep = [];
        for (var i = 0; i < files.length; i++) {
            var comm = readComm(files[i]).toLowerCase();
            var dirty = false;
            for (var k = 0; k < THREAD_NAME_NEEDLES.length; k++) {
                if (comm.indexOf(THREAD_NAME_NEEDLES[k]) !== -1) { dirty = true; break; }
            }
            if (dirty) {
                console.log("[bypass] hiding /proc/self/task tid " + files[i].getName() + " (comm=" + comm + ")");
                continue;
            }
            keep.push(files[i]);
        }
        return Java.array("java.io.File", keep);
    };


    Process.setExceptionHandler(function (details) {
        console.log("[bypass] swallowed native exception: " + JSON.stringify(details));
        return true;
    });

    console.log("[bypass] Demo 4 hooks installed (Manager-pkg, NATIVE modifier, /proc/self/maps, stack & thread probes).");
});
