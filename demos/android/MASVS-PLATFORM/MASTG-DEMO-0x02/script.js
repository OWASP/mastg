// Dumps the merged Jetpack Compose semantics tree of the focused window.
// Call dumpComposeViews() in the Frida REPL after opening a screen.

const DUMP_DELAY_MS = 1500;
const BACKTRACE_LINES = 6;
let pendingDump = null;

function describe(node, Entry, SemanticsPropertyKey) {
    const properties = [];
    const iterator = node.getConfig().iterator();
    let sensitivity = "null";

    while (iterator.hasNext()) {
        const entry = Java.cast(iterator.next(), Entry);
        const key = Java.cast(entry.getKey(), SemanticsPropertyKey).getName().toString();
        const value = entry.getValue();

        if (key === "IsSensitiveData") {
            sensitivity = value.toString();
        } else if (key === "Text" || key === "ContentDescription") {
            properties.push(`${key}=${value}`);
        } else if (key === "EditableText" || key === "Password" || key === "OnClick") {
            properties.push(key);
        }
    }

    return `id=${node.getId()} isSensitiveData=${sensitivity}` +
        (properties.length ? ` ${properties.join(" ")}` : "");
}

function dumpComposeViews() {
    Java.perform(() => {
        Java.scheduleOnMainThread(() => {
            try {
                const Node = Java.use("androidx.compose.ui.semantics.SemanticsNode");
                const Entry = Java.use("java.util.Map$Entry");
                const Key = Java.use("androidx.compose.ui.semantics.SemanticsPropertyKey");
                let roots = 0;

                function walk(node, depth, lines) {
                    lines.push(`${"  ".repeat(depth)}${describe(node, Entry, Key)}`);
                    const children = node.getChildren();
                    for (let i = 0; i < children.size(); i++) {
                        walk(Java.cast(children.get(i), Node), depth + 1, lines);
                    }
                }

                Java.choose("androidx.compose.ui.platform.AndroidComposeView", {
                    onMatch(view) {
                        if (!view.isAttachedToWindow() || !view.isShown() || !view.hasWindowFocus()) {
                            return;
                        }
                        roots++;
                        const lines = [];
                        walk(view.getSemanticsOwner().getRootSemanticsNode(), 0, lines);
                        console.log("\n[*] Merged Compose semantics tree of the focused window:");
                        console.log(lines.join("\n"));
                    },
                    onComplete() {
                        if (roots === 0) {
                            console.log("[-] No attached Compose root found in the focused window.");
                        } else {
                            console.log("[*] Finished dumping Compose semantics.");
                        }
                    }
                });
            } catch (e) {
                console.log(`[-] Unable to dump Compose semantics: ${e}`);
            }
        });
    });
}

function scheduleDump() {
    if (pendingDump !== null) {
        clearTimeout(pendingDump);
    }
    pendingDump = setTimeout(() => {
        pendingDump = null;
        dumpComposeViews();
    }, DUMP_DELAY_MS);
}

globalThis.dumpComposeViews = dumpComposeViews;

Java.perform(() => {
    try {
        const SemanticsProperties = Java.use("androidx.compose.ui.semantics.SemanticsPropertiesKt");
        const setter = SemanticsProperties.setSensitiveData.overload(
            "androidx.compose.ui.semantics.SemanticsPropertyReceiver", "boolean"
        );
        const Log = Java.use("android.util.Log");
        const Exception = Java.use("java.lang.Exception");

        setter.implementation = function (receiver, value) {
            console.log(`\n[setSensitiveData] isSensitiveData=${value}`);
            const lines = Log.getStackTraceString(Exception.$new()).split("\n").slice(1, 1 + BACKTRACE_LINES);
            console.log(lines.map(line => "    " + line.trim()).join("\n"));
            const result = setter.call(this, receiver, value);
            scheduleDump();
            return result;
        };
        console.log("[*] Hook installed. Tap Start, or call dumpComposeViews() for the focused screen.");
    } catch (e) {
        console.log(`[-] Compose sensitivity hook unavailable: ${e}`);
        console.log("[*] You can still try dumpComposeViews() after opening a Compose screen.");
    }
});
