// Hooks the View setters that configure accessibility data sensitivity and dumps the view hierarchy of the
// foreground activity with the effective isAccessibilityDataSensitive() value of each view.
// The dump runs automatically shortly after the app changes these settings. You can also call dumpViews()
// from the Frida REPL at any time, for example, after navigating to another screen that handles sensitive data.

const SENSITIVITY_MODES = { 0: "AUTO", 1: "YES", 2: "NO" };
const BACKTRACE_LINES = 6;
const DUMP_DELAY_MS = 1500;

let pendingDump = null;

function backtrace() {
    const Log = Java.use("android.util.Log");
    const Exception = Java.use("java.lang.Exception");
    const lines = Log.getStackTraceString(Exception.$new()).split("\n").slice(1, 1 + BACKTRACE_LINES);
    return lines.map(line => "    " + line.trim()).join("\n");
}

function describe(view) {
    const TextView = Java.use("android.widget.TextView");
    const EditText = Java.use("android.widget.EditText");
    let description = view.getClass().getName();

    const id = view.getId();
    if (id !== -1) {
        try {
            description += ` id=${view.getResources().getResourceEntryName(id)}`;
        } catch (e) {
            // The view uses a generated ID without a resource entry.
        }
    }

    if (TextView.class.isInstance(view)) {
        const textView = Java.cast(view, TextView);
        const hint = textView.getHint();
        if (hint !== null) {
            description += ` hint="${hint.toString()}"`;
        }
        // Don't print the content of input fields to avoid logging sensitive data.
        if (!EditText.class.isInstance(view)) {
            const text = textView.getText();
            if (text !== null && text.length() > 0) {
                description += ` text="${text.toString()}"`;
            }
        }
    }
    return description;
}

function walk(view, depth, lines) {
    const ViewGroup = Java.use("android.view.ViewGroup");
    lines.push(
        `${"  ".repeat(depth)}${describe(view)}` +
        ` isAccessibilityDataSensitive=${view.isAccessibilityDataSensitive()}` +
        ` filterTouchesWhenObscured=${view.getFilterTouchesWhenObscured()}`
    );
    if (ViewGroup.class.isInstance(view)) {
        const group = Java.cast(view, ViewGroup);
        for (let i = 0; i < group.getChildCount(); i++) {
            walk(group.getChildAt(i), depth + 1, lines);
        }
    }
}

function dumpViews() {
    Java.perform(() => {
        Java.scheduleOnMainThread(() => {
            Java.choose("android.app.Activity", {
                onMatch(activity) {
                    if (activity.isFinishing() || activity.isDestroyed() || !activity.hasWindowFocus()) {
                        return;
                    }
                    const lines = [];
                    walk(activity.getWindow().getDecorView(), 0, lines);
                    console.log(`\n[*] View hierarchy of ${activity.getClass().getName()}:`);
                    console.log(lines.join("\n"));
                },
                onComplete() {
                    console.log("\n[*] Finished dumping the view hierarchy.");
                }
            });
        });
    });
}

function scheduleDump() {
    if (pendingDump !== null) {
        clearTimeout(pendingDump);
    }
    pendingDump = setTimeout(() => {
        pendingDump = null;
        dumpViews();
    }, DUMP_DELAY_MS);
}

globalThis.dumpViews = dumpViews;

Java.perform(() => {
    const View = Java.use("android.view.View");

    if (View.setAccessibilityDataSensitive === undefined || View.isAccessibilityDataSensitive === undefined) {
        console.log("[-] Accessibility data sensitivity APIs aren't available. Use a device running Android 14 (API level 34) or higher.");
        return;
    }

    View.setAccessibilityDataSensitive.implementation = function (mode) {
        console.log(`\n[setAccessibilityDataSensitive] ${describe(this)} mode=${SENSITIVITY_MODES[mode] ?? mode}`);
        console.log(backtrace());
        this.setAccessibilityDataSensitive(mode);
        scheduleDump();
    };

    View.setFilterTouchesWhenObscured.implementation = function (enabled) {
        console.log(`\n[setFilterTouchesWhenObscured] ${describe(this)} enabled=${enabled}`);
        console.log(backtrace());
        this.setFilterTouchesWhenObscured(enabled);
        scheduleDump();
    };

    console.log("[*] Hooks installed. Tap Start, or call dumpViews() to dump the current screen.");
});
