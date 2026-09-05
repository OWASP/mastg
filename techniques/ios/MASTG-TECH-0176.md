---
title: Monitoring Universal Link Handlers at Runtime with Frida
platform: ios
---

Use this technique to dynamically identify which method receives an incoming universal link, what data it carries, and how the app subsequently opens or forwards the URL. This complements static analysis (see @MASTG-KNOW-0080) and is especially useful without source code, since you can recover the full URL and the methods involved at runtime. To trigger the universal links you analyze here, see @MASTG-TECH-0169.

## Tracing the Link Receiver Method

When iOS opens an app from a universal link, it delivers an `NSUserActivity` to the app's continuation method (`application:continueUserActivity:restorationHandler:` or the scene-based `scene:continueUserActivity:`). Use `frida-trace` from @MASTG-TOOL-0039 to instrument it:

```bash
frida-trace -U <App Name> -m "*[* *continueUserActivity*]"
```

Due to the use of wildcards, this pattern will cover both APIs.

Trigger the universal link (for example, from the Notes app) and confirm the method is called. Then edit the generated stub in `__handlers__/` to print the activity details, including the verified URL in `webpageURL`:

```javascript
  onEnter: function (log, args, state) {
    log("-[AppDelegate application: " + args[2] + " continueUserActivity: " + args[3] +
        " restorationHandler: " + args[4] + "]");
    log("\tactivityType: " + ObjC.Object(args[3]).activityType().toString());
    log("\twebpageURL: " + ObjC.Object(args[3]).webpageURL().toString());
    log("\tuserInfo: " + ObjC.Object(args[3]).userInfo().toString());
  },
```

The output reveals the `webpageURL` the user accessed, the `activityType` (`NSUserActivityTypeBrowsingWeb` for universal links), and any data carried in `userInfo`. This is the input the app's handler acts on, and the starting point for reverse engineering how that input is validated and used.

## Checking How the Links Are Opened

The receiver method often does not open the URL itself but rather delegates it to another method. Extend the trace to include any function that opens a URL:

```bash
frida-trace -U <App Name> -m "*[* *continueUserActivity*]" -i "*open*Url*"
```

Trigger the universal link again and observe which additional functions run. For Swift symbols, demangle them to recover the class, method name, and parameter types (see @MASTG-TECH-0114).

Use the demangled signature to edit the corresponding stub and print the relevant arguments (for example, the `url` parameter), so you can follow the URL from the receiver method to the code that ultimately acts on it.

## Inspecting Outgoing Universal Links

An app can also open universal links in other apps with `open(_:options:completionHandler:)` (the modern replacement for `openURL:options:completionHandler:`). Trace these calls to check whether the app forwards sensitive data to other apps through outgoing links:

```bash
frida-trace -U <App Name> -m "-[UIApplication open*]"
```

Inspect the URL argument to see what is being sent. For background on limiting sensitive data exposure through these channels, see @MASTG-BEST-0045.

## A Note on Handoff

Handoff (see @MASTG-KNOW-0123) delivers its `NSUserActivity` through the **same** continuation method as universal links, but with an app-defined `activityType` and data in `userInfo` rather than a `webpageURL`. The hooks above therefore also capture Handoff continuations; use the `activityType` to distinguish them from universal links (`NSUserActivityTypeBrowsingWeb`).
