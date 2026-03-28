---
platform: ios
title: References to File Access in WebViews with radare2
id: MASTG-DEMO-0098
code: [swift]
test: MASTG-TEST-0335
---

## Sample

This sample demonstrates a WKWebView with the undocumented `allowFileAccessFromFileURLs` property enabled, allowing JavaScript to read other local `file://` URLs even when `loadFileURL:allowingReadAccessToURL:` is intentionally restricted.

{{ MastgTest.swift }}

The sample sets up two sibling directories under `cachesDirectory/demoRoot/`:

- `app/`: contains `index.html` (the loaded page) and `api-key.txt` (a sensitive secret).
- `other/`: contains `other.html` (a page in a separate directory).

Using KVC, `allowFileAccessFromFileURLs` is set to `true` and `allowUniversalAccessFromFileURLs` is left `false`. The WebView loads `index.html` with `loadFileURL(_:allowingReadAccessTo:)`, intentionally passing `indexURL` as both arguments so that native WebKit file access is restricted to just that single file.

Despite this restriction, `index.html` contains JavaScript that demonstrates three access methods:

- `fetch("./api-key.txt")`: reads `api-key.txt` from the same `app/` directory.
- `XMLHttpRequest` to `../other/other.html`: reads a file from the sibling `other/` directory.
- `<iframe src="../other/other.html">`: embeds the sibling file directly.

The `allowingReadAccessTo: indexURL` choice is intentional: it shows that even when native WebKit file loading is locked down to a single file, `allowFileAccessFromFileURLs = true` is enough for JavaScript to reach any local `file://` URL via `fetch` or XHR. The `allowingReadAccessTo` parameter is not the target of this demo; it's included to illustrate the scope of what this flag bypasses.

## Steps

1. Extract the app binary from the IPA (@MASTG-TECH-0054).
2. Run @MASTG-TOOL-0073 (radare2) using the provided script to search for references to the relevant WebView methods.

{{ webview_file_access.r2 # run.sh }}

The script searches for:

- References to `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs`.
- References to the `loadFileURL:allowingReadAccessToURL:` method.

## Observation

The output shows all cross-references and disassembled snippets.

{{ output.txt # function.asm # # function2.asm}}

## Evaluation

The test **fails** because the binary contains a reference to `allowFileAccessFromFileURLs` that is set to `true`, and the binary also calls `loadFileURL:allowingReadAccessToURL:`.

In `sym.func.100004ad8` (the `showWebView` function), around `0x100004b54`, we can see that the app sets `allowFileAccessFromFileURLs = true`. This follows the classic Swift-to-Objective-C bridging pattern:

- `mov w0, 1` loads boolean `true`, then bridges it to an `NSNumber` via `objc_retainAutoreleasedReturnValue`.
- At `0x100004b64`, `"allowFileAccessFromFileURLs"` is constructed as an `NSString`.
- `setValue:forKey:` is called via `fcn.10000c780` at `0x100004b8c`.

Immediately after, around `0x100004b9c`, the app sets `allowUniversalAccessFromFileURLs = false` using the same pattern:

- `mov w0, 0` loads boolean `false`, bridged to `NSNumber`.
- At `0x100004bac`, `"allowUniversalAccessFromFileURLs"` is constructed as an `NSString`.
- `setValue:forKey:` is called at `0x100004bd0`.

The actual call to `loadFileURL:allowingReadAccessToURL:` is inside the `presenter.present` completion closure, compiled into `sym.func.1000050c0`. The key sequence immediately before the call at `0x10000520c` is:

```text
0x1000051ac      bl sym.func.100007be4               ; resolve the stored indexURL static
0x1000051b0      mov x23, x0
0x1000051b4      mov x0, x22
0x1000051b8      mov x1, x23
0x1000051bc      mov x2, x21
0x1000051c0      blr x26                             ; initialize Swift URL in x22 from x23
0x1000051c8      bl Foundation.URL._bridgeToObjectiveC.NSURL  ; bridge to NSURL
0x1000051cc      mov x24, x0                         ; x24 = NSURL(indexURL)  [fileURL arg]
...
0x1000051dc      mov x0, x22
0x1000051e0      mov x1, x23                         ; same URL data as before
0x1000051e4      mov x2, x21
0x1000051e8      blr x26                             ; re-initialize Swift URL in x22 from same x23
0x1000051ec      bl Foundation.URL._bridgeToObjectiveC.NSURL  ; bridge again
0x1000051f0      mov x20, x0                         ; x20 = NSURL(indexURL)  [readAccess arg]
...
0x100005204      mov x2, x24                         ; fileURL = NSURL(indexURL)
0x100005208      mov x3, x20                         ; allowingReadAccessTo = NSURL(indexURL)
0x10000520c      bl fcn.10000c680                    ; loadFileURL:allowingReadAccessToURL:
```

Both `x2` and `x3` are produced by calling `Foundation.URL._bridgeToObjectiveC` on the same Swift `URL` value: x22 is initialized with the same source (x23, the resolved `indexURL` static property) in both cases. This confirms that `loadFileURL` is called with `indexURL` as both the first argument (the file to load) and the second argument (the read-access boundary), as written in the Swift source.

Although `allowingReadAccessTo: indexURL` restricts native WebKit file loading to just the `index.html` file, `allowFileAccessFromFileURLs = true` independently grants JavaScript the ability to issue `fetch()` and `XMLHttpRequest` calls to other `file://` URLs. This means JavaScript running in the WebView can read `api-key.txt` and `other.html` regardless of the `allowingReadAccessTo` restriction.
