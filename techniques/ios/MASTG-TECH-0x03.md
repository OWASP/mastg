---
title: Bypassing Jailbreak Detection
platform: ios
---

Bypassing jailbreak detection involves identifying the specific checks an app performs at runtime and then nullifying them, either through automated tools or manual reverse engineering with dynamic hooks.

## Automated Bypass

The quickest way to bypass common jailbreak detection mechanisms is @MASTG-TOOL-0038. You can find the implementation of the jailbreak bypass in the [jailbreak.ts script](https://github.com/sensepost/objection/blob/master/agent/src/ios/jailbreak.ts "jailbreak.ts").

## Manual Bypass

If the automated bypasses aren't effective you need to get your hands dirty and reverse engineer the app binaries until you find the pieces of code responsible for the detection and either patch them statically or apply runtime hooks to disable them.

**Step 1: Reverse Engineering:**

When you need to reverse engineer a binary looking for jailbreak detection, the most obvious way is to search for known strings, such as "jail" or "jailbreak". Note that this won't always be effective, especially when resilience measures are in place or when the developer has avoided such obvious terms.

Example: Download the @MASTG-APP-0024, unzip it, load the main binary into @MASTG-TOOL-0073 and wait for the analysis to complete.

```sh
r2 -A ./DVIA-v2-swift/Payload/DVIA-v2.app/DVIA-v2
```

Now you can list the binary's symbols using the `is` command and apply a case-insensitive grep (`~+`) for the string "jail".

```sh
[0x1001a9790]> is~+jail
...
2230  0x001949a8 0x1001949a8 GLOBAL FUNC 0        DVIA_v2.JailbreakDetectionViewController.isJailbroken.allocator__Bool
7792  0x0016d2d8 0x10016d2d8 LOCAL  FUNC 0        +[JailbreakDetection isJailbroken]
...
```

As you can see, there's an instance method with the signature `-[JailbreakDetectionVC isJailbroken]`.

**Step 2: Dynamic Hooks:**

Now you can use Frida to bypass jailbreak detection by performing the so-called early instrumentation, that is, by replacing function implementation right at startup.

Use `frida-trace` on your host computer:

```bash
frida-trace -U -f /Applications/DamnVulnerableIOSApp.app/DamnVulnerableIOSApp  -m "-[JailbreakDetectionVC isJailbroken]"
```

This will start the app, trace calls to `-[JailbreakDetectionVC isJailbroken]`, and create a JavaScript hook for each matching element.

Open `./__handlers__/__JailbreakDetectionVC_isJailbroken_.js` with your favorite editor and edit the `onLeave` callback function. You can simply replace the return value using `retval.replace()` to always return `0`:

```javascript
onLeave: function (log, retval, state) {
    console.log("Function [JailbreakDetectionVC isJailbroken] originally returned:"+ retval);
    retval.replace(0);
    console.log("Changing the return value to:"+retval);
}
```

This will provide the following output:

```bash
$ frida-trace -U -f /Applications/DamnVulnerableIOSApp.app/DamnVulnerableIOSApp  -m "-[JailbreakDetectionVC isJailbroken]:"

Instrumenting functions...                                           `...
-[JailbreakDetectionVC isJailbroken]: Loaded handler at "./__handlers__/__JailbreakDetectionVC_isJailbroken_.js"
Started tracing 1 function. Press Ctrl+C to stop.

Function [JailbreakDetectionVC isJailbroken] originally returned:0x1
Changing the return value to:0x0
```
