console.log("\n[*] Starting HealthKit entitlement-backed API tracing...\n");

function printBacktrace(context, maxLines) {
    console.log("\nBacktrace:");
    const backtrace = Thread.backtrace(context, Backtracer.ACCURATE)
        .map(DebugSymbol.fromAddress);

    for (let i = 0; i < Math.min(maxLines, backtrace.length); i++) {
        console.log(backtrace[i]);
    }
}

function describeObjCObject(value) {
    if (value.isNull()) {
        return "nil";
    }

    try {
        return new ObjC.Object(value).toString();
    } catch (e) {
        return value.toString();
    }
}

function recordCall(name, context, details) {
    console.log("\n[+] " + name + " called");
    if (details !== null && details !== undefined && details !== "") {
        console.log("    " + details);
    }
    printBacktrace(context, 6);
}

function hookMethod(className, selector, displayName, detailCallback, leaveCallback) {
    const klass = ObjC.classes[className];
    if (!klass) {
        console.log("[-] Failed to hook " + displayName + ": class not found");
        return false;
    }

    const method = klass[selector];
    if (!method) {
        console.log("[-] Failed to hook " + displayName + ": method not found");
        return false;
    }

    try {
        Interceptor.attach(method.implementation, {
            onEnter(args) {
                const context = this.context;
                const details = detailCallback ? detailCallback(args) : null;
                recordCall(displayName, context, details);
            },
            onLeave(retval) {
                if (leaveCallback) {
                    leaveCallback(retval);
                }
            }
        });
    } catch (e) {
        console.log("[-] Failed to hook " + displayName + ": " + e);
        return false;
    }

    console.log("[*] Hooked " + displayName);
    return true;
}

if (ObjC.available) {
    if (!ObjC.classes.HKHealthStore) {
        console.log("[-] Failed to hook HKHealthStore: class not found");
    } else {
        hookMethod(
            "HKHealthStore",
            "+ isHealthDataAvailable",
            "HKHealthStore.isHealthDataAvailable()",
            null,
            (retval) => console.log("    Return value: " + retval.toInt32())
        );

        hookMethod(
            "HKHealthStore",
            "- requestAuthorizationToShareTypes:readTypes:completion:",
            "HKHealthStore.requestAuthorization(toShare:read:completion:)",
            (args) => "typesToShare=" + describeObjCObject(args[2]) + ", typesToRead=" + describeObjCObject(args[3]),
            null
        );

        hookMethod(
            "HKHealthStore",
            "- authorizationStatusForType:",
            "HKHealthStore.authorizationStatus(for:)",
            (args) => "type=" + describeObjCObject(args[2]),
            (retval) => console.log("    Return value: " + retval.toInt32())
        );

        hookMethod(
            "HKHealthStore",
            "- getRequestStatusForAuthorizationToShareTypes:readTypes:completion:",
            "HKHealthStore.getRequestStatusForAuthorization(toShare:read:completion:)",
            (args) => "typesToShare=" + describeObjCObject(args[2]) + ", typesToRead=" + describeObjCObject(args[3]),
            null
        );

        hookMethod(
            "HKHealthStore",
            "- executeQuery:",
            "HKHealthStore.execute(_:)",
            (args) => "query=" + describeObjCObject(args[2]),
            null
        );

        hookMethod(
            "HKHealthStore",
            "- saveObject:withCompletion:",
            "HKHealthStore.save(_:withCompletion:)",
            (args) => "object=" + describeObjCObject(args[2]),
            null
        );

        hookMethod(
            "HKHealthStore",
            "- saveObjects:withCompletion:",
            "HKHealthStore.save(_:withCompletion:) [array]",
            (args) => "objects=" + describeObjCObject(args[2]),
            null
        );
    }
} else {
    console.log("[-] Objective-C runtime is not available.");
}

console.log("\n[*] Hook setup finished. Interact with the app to trigger HealthKit API calls.\n");
