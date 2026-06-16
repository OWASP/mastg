---
platform: ios
title: HTML Injection in a Local WebView Leading to Local File Access
code: [swift]
id: MASTG-DEMO-0096
test: MASTG-TEST-0333
kind: fail
---

## Sample

This sample demonstrates how overly broad file read access in a WebView can increase the impact of a separate HTML injection flaw. The app loads a trusted local HTML file and grants the WebView read access to the entire `Documents` directory by calling [`loadFileURL(_:allowingReadAccessTo:)`](https://developer.apple.com/documentation/webkit/wkwebview/loadfileurl(_:allowingreadaccessto:)).

By itself, that broad read access is not enough to expose files. The issue becomes exploitable because the page also reads the `username` parameter from the URL and inserts it into the DOM using `innerHTML`. Since attacker-controlled input is treated as HTML, an attacker can inject markup that loads other local files from the same directory. See @MASTG-DEMO-0095 for more details on the HTML injection aspect of the vulnerability.

Because the WebView can read the full `Documents` directory, injected elements such as an `<iframe>` can load sibling files like `secret.txt`. This shows how overly broad local file access can turn a separate WebView injection bug into a local file disclosure issue.

To exploit the demo and load the secret file stored at `<container>/Documents/secret.txt`, you can enter the following payload as input:

- `<iframe src="./secret.txt"></iframe>`
- `<object data="./secret.txt" type="text/plain"></object>`
- `<embed src="./secret.txt" type="text/plain">`

Summary of steps leading to this vulnerability.

1. The app loads a local HTML file into a `WKWebView`.
2. The WebView is granted read access to the entire `Documents` directory.
3. The page reads attacker controlled input from the `username` query parameter.
4. That value is inserted into the DOM using `innerHTML`.
5. The attacker injects markup that loads another local file, such as `secret.txt`.
6. As a result, local content becomes readable inside the WebView.

The vulnerable code path is the combination of untrusted input being inserted with `innerHTML` and broader local file read access than necessary. Together, they allow attacker controlled markup to access local files that should not be exposed.

{{ ../MASTG-DEMO-0095/MastgTest.swift # ../MASTG-DEMO-0095/ai-decompiled.swift }}

## Steps

1. Unzip the app package and locate the main binary file as described in @MASTG-TECH-0058. In this case, the binary is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 and inspect the code paths that create and load the `WKWebView`.
3. Identify any calls that load content into the WebView, such as `load(_:)` or `loadFileURL(_:allowingReadAccessTo:)`.
4. Obtain the code that updates the page content after loading, to verify whether attacker-controlled input is inserted into the DOM using `innerHTML`.

{{ load_webview.r2 # run.sh }}

## Observation

The output shows the `loadFileURL:allowingReadAccessToURL:` call site, the `docDir` and `fileURL` lazy initializers, and the `innerHTML` assignment in the embedded HTML template.

{{ ../MASTG-DEMO-0095/showWebView.asm # output.txt }}

{{ docDir-init.asm # fileURL-init.asm }}

## Evaluation

The test fails because the app grants the WebView read access to the entire `Documents` directory using `loadFileURL(_:allowingReadAccessTo:)`. `docDir` points to the app's `Documents` directory, and it's passed directly as `allowingReadAccessTo` in `webView.loadFileURL(url, allowingReadAccessTo: docDir)`. This grants the WebView read access to the entire `Documents` directory, which also contains `secret.txt` (written in `createSecretFile`). An attacker can inject `<iframe src='./secret.txt'></iframe>` as their name to expose this file.

The attack succeeds because of the combination of this overly broad read access and the fact that attacker controlled input is inserted into the page. The decompiled code and local HTML show that attacker-controlled input (the `username` value) is inserted into the page by assigning it directly to `innerHTML` via JavaScript. Because this value is not HTML-escaped, tags such as `<iframe>`, `<img>`, and `<script>` are interpreted as markup, allowing the attacker's payload to be rendered as HTML. See @MASTG-DEMO-0095 for more details on the HTML injection aspect of the vulnerability.

The following analysis complements the one in @MASTG-DEMO-0095 by focusing on the overly broad file access aspect of the vulnerability.

### AI-Decompiled Code Analysis

!!! note "About `ai-decompiled.swift`"
    The `ai-decompiled.swift` file is an AI-assisted reconstruction derived from `showWebView.asm`, `docDir-init.asm`, and `fileURL-init.asm` and is provided only as a convenience for understanding the logic. It may be inaccurate or incomplete; the assembly and the original binary are the authoritative sources for analysis.

1. On **lines 10–12**, `docDir` is initialized to `FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!`, which resolves to the app's `Documents` directory — the same directory that contains `secret.txt`.
2. On **lines 14–16**, `fileURL` is built by appending `"index.html"` directly to `docDir`, so the loaded HTML file and any sensitive sibling files share the same parent directory.
3. On **line 35**, `webView.loadFileURL(url, allowingReadAccessTo: docDir)` passes `docDir` as the read-access root. Because `docDir` is the entire `Documents` directory, the WebView can reach any file inside it, including `secret.txt`.

### Disassembly Analysis

We can see the call to `loadFileURL:allowingReadAccessToURL:` at `0x100004f20` (in `showWebView.asm`). To determine which path is being granted read access, we need to trace both arguments.

Just before the call, the arguments are arranged as follows (from `showWebView.asm`):

```text
0x100004f10      ldr x1, [x8, 0x118]   ; reloc.fixup.loadFileURL:allowingReadAccessToURL:
0x100004f14      mov x0, x25           ; WKWebView instance
0x100004f18      mov x2, x26           ; urlWithUsername (NSURL)
0x100004f1c      mov x3, x20           ; allowingReadAccessTo (NSURL, = docDir)
0x100004f20      bl sym.imp.objc_msgSend
```

**Tracing `x3` (the `allowingReadAccessTo` argument):**

`x20` is set at `0x100004f08` by bridging a Swift lazy static `URL` to an `NSURL`. Rather than relying on the developer-assigned symbol name, we locate its initializer through the iOS API it calls: `axt @ 0x100018130` (the `URLsForDirectory:inDomains:` reloc) leads directly to `func.100004000` (the xref result is in `output.txt`; the disassembly is in `docDir-init.asm`).

In `func.100004000` (from `docDir-init.asm`), the key sequence is:

```text
0x100004070      ldr x0, [x8, 0x1a0]   ; reloc.NSFileManager class
0x100004074      bl sym.imp.objc_opt_self
0x10000407c      ldr x1, [x8, 0x128]   ; reloc.fixup.defaultManager selector
0x100004080      bl sym.imp.objc_msgSend
...
0x100004094      ldr x1, [x8, 0x130]   ; URLsForDirectory:inDomains: selector
0x100004098      mov w2, 9
0x10000409c      mov w3, 1
0x1000040a0      bl sym.imp.objc_msgSend
```

This is the pattern for calling:

`FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)`

because:

- `9` is `NSDocumentDirectory`
- `1` is `NSUserDomainMask`

The function bridges the returned `NSArray` to a Swift array and stores its first element as the `docDir` lazy static. This confirms that `x3` (the `allowingReadAccessTo` argument) is the app's `Documents` directory.

You can find the enum values above in `/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/System/Library/Frameworks/Foundation.framework/Headers/NSPathUtilities.h`.

```c
typedef NS_ENUM(NSUInteger, NSSearchPathDirectory) {
    NSApplicationDirectory = 1,             // supported applications (Applications)
    NSDemoApplicationDirectory,             // unsupported applications, demonstration versions (Demos)
    NSDeveloperApplicationDirectory,        // developer applications (Developer/Applications). DEPRECATED - there is no one single Developer directory.
    NSAdminApplicationDirectory,            // system and network administration applications (Administration)
    NSLibraryDirectory,                     // various documentation, support, and configuration files, resources (Library)
    NSDeveloperDirectory,                   // developer resources (Developer) DEPRECATED - there is no one single Developer directory.
    NSUserDirectory,                        // user home directories (Users)
    NSDocumentationDirectory,               // documentation (Documentation)
    NSDocumentDirectory,                    // documents (Documents)
...
typedef NS_OPTIONS(NSUInteger, NSSearchPathDomainMask) {
    NSUserDomainMask = 1,       // user's home directory --- place to install user's personal items (~)
    NSLocalDomainMask = 2,      // local to the current machine --- place to install items available to everyone on this machine (/Library)
    NSNetworkDomainMask = 4,    // publicly available location in the local area network --- place to install items available on the network (/Network)
    NSSystemDomainMask = 8,     // provided by Apple, unmodifiable (/System)
    NSAllDomainsMask = 0x0ffff  // all domains: all of the above and future items
};
```

**Tracing `x2` (the file URL argument):**

`x26` is set at `0x100004edc` by bridging a Swift lazy static `URL` to an `NSURL`. We locate its initializer via the iOS API it calls: `axt @ 0x10000a738` (the `appendingPathComponent` import stub) leads to `func.10000418c` (the materializer `ZTm_`). `axt @ 0x10000418c` then reveals its swift_once guard `func.100004144` (`Z_`) (all xref results are in `output.txt`). `Z_` encodes the filename `"index.html"` inline as immediate character constants (from `fileURL-init.asm`, which also includes `ZTm_` appended after):

```text
0x10000414c      mov x2, 0x6e69                            ; 'in'
0x100004150      movk x2, 0x6564, lsl 16                   ; 'de'
0x100004154      movk x2, 0x2e78, lsl 32                   ; 'x.'
0x100004158      movk x2, 0x7468, lsl 48                   ; 'ht'
0x10000415c      mov x3, 0x6c6d                            ; 'ml'
```

The `ZTm_` materializer then loads the `docDir` lazy static and passes the encoded filename to `appendingPathComponent`:

```text
0x100004200      ldr x8, [x8, 0x1b0]   ; sym.MASTestApp.MastgTest.docDir._6E8AB2C58CE173A727EF27CB85DF8CD8_...z_
...
0x100004240      bl sym.imp.Foundation.URL.appendingPathComponent_...CSSF_
```

This confirms that `fileURL` is built by appending `"index.html"` to `docDir`. Therefore, the app loads `index.html` from the `Documents` directory and grants the WebView read access to the **entire `Documents` directory**.

### How to Fix

To prevent this:

- Move `index.html` into a dedicated subdirectory (for example, `Library/Application Support/webContent/`) and pass that subdirectory as `allowingReadAccessTo` instead of the full `Documents` directory. This ensures the WebView can't reach `secret.txt` or any other user data stored in `Documents`.
- Replace `innerHTML` with `textContent` in the JavaScript injection so that user input is always treated as plain text, not markup.

{{ fix.diff }}

To apply the fix from `fix.diff` to the original `MastgTest.swift` file, run the following command from this demo directory:

```sh
patch ../MASTG-DEMO-0095/MastgTest.swift -o MastgTest-fixed.swift < fix.diff
```
