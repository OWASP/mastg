---
title: Hardening Against Reverse Engineering Tools
alias: hardening-against-reverse-engineering-tools
id: MASTG-BEST-0048
platform: ios
knowledge: [MASTG-KNOW-0087]
---

Defending against reverse engineering tools on iOS requires a layered approach that combines several types of security controls:

- **Detective controls**: Scan for known reverse engineering tool artifacts (@MASTG-KNOW-0087), such as checking loaded dynamic libraries via `_dyld_image_count`/`_dyld_get_image_name` for names containing "frida", "gadget", "cynject", or other tool-specific strings. This technique is effective against Frida Gadget (embedded mode) and tools loaded through dyld, but on official builds frida-server injects its agent using a [custom Mach-O loader](https://github.com/frida/frida-gum/blob/main/gum/backend-darwin/gumdarwinmapper.c) that bypasses dyld, so it does not detect frida-server in injected mode. Additionally, probe TCP port 27042 for a D-Bus authentication response to reveal a running frida-server.
- **Deterrent controls**: Obfuscate detection logic (@MASTG-KNOW-0089), scatter checks throughout the app, and vary their timing to increase the cost and effort required to bypass these checks. Avoid centralizing detection in a single function, as a fixed entry point can be patched or hooked.
- **Responsive controls**: Terminate the app immediately, clear sensitive data from memory, or alert the backend server when a tool is detected.

## Detective Controls

### Inspect Loaded Dynamic Libraries

Use [`_dyld_image_count()`](https://developer.apple.com/documentation/kernel/1582893-_dyld_image_count) and [`_dyld_get_image_name()`](https://developer.apple.com/documentation/kernel/1582851-_dyld_get_image_name) to iterate over all loaded dynamic libraries and check for names associated with reverse engineering tools.

Known artifact names to look for include `FridaGadget`, `frida-agent`, `cynject`, and similar strings. `_dyld_image_count` only returns images [tracked by dyld](https://github.com/apple-oss-distributions/dyld/blob/main/dyld/DyldAPIs.cpp), so this technique is effective against Frida Gadget (embedded mode) and tools loaded through dyld such as via `DYLD_INSERT_LIBRARIES`. However, on official builds frida-server injects its agent using a [custom Mach-O loader](https://github.com/frida/frida-gum/blob/main/gum/backend-darwin/gumdarwinmapper.c) that bypasses dyld, and its [injector prioritizes the mapper over dyld](https://github.com/frida/frida-core/blob/main/src/darwin/fruitjector.vala), rendering this technique ineffective for the most common jailbroken scenario. An attacker can also bypass a name-based check by renaming the library, so combine this with the D-Bus port detection technique discussed below for better coverage.

### Apply Multiple Detection Techniques

Layer several techniques to maximize detection coverage:

- **Library name scanning**: Iterate loaded dylibs using `_dyld_image_count`/`_dyld_get_image_name` for known artifact names (see [Inspect Loaded Dynamic Libraries](#inspect-loaded-dynamic-libraries) above for its coverage and limitations).
- **TCP port probing**: Check whether port 27042 is open and responds to a D-Bus AUTH message, which reveals a default frida-server configuration.
- **Named pipe detection**: Scan for named pipes used by frida-server for inter-process communication.

See @MASTG-KNOW-0087 for a detailed discussion of each technique and its bypass difficulty.

## Deterrent Controls

### Implement Detection in Native Code

Write detection checks in C rather than Swift or Objective-C. Native code is significantly harder to hook than Swift/Objective-C, which can be intercepted via Frida's `ObjC` or `Swift` APIs. When bridging results back to the application layer via native calls, be aware that the bridging call itself can be hooked.

### Obfuscate Detection Logic

Apply code obfuscation (@MASTG-KNOW-0089) across detection routines. Scatter checks throughout the application instead of centralizing them, and vary their timing — for example, periodically, on random events, or immediately before sensitive operations — to prevent systematic bypassing.

### Inline Detection Logic

Avoid placing detection code in dedicated, named functions. A named function has a fixed entry point that can be patched with a single hook. Inlining the detection logic directly at each call site removes that fixed target.

## Responsive Controls

When a reverse engineering tool is detected:

- Terminate the app session immediately.
- Clear sensitive data from memory.
- Alert the backend server to flag the compromised session.

Do not allow the app to continue running in a compromised state. Protect the response mechanism itself by implementing it in native code and obfuscating its control flow.
